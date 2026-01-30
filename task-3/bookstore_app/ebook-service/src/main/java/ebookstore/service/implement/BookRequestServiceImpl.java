package ebookstore.service.implement;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.exception.DatabaseException;
import ebookstore.exception.RequestNotFoundException;
import ebookstore.exception.message.RequestErrorMessages;
import ebookstore.mapper.RequestMapper;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.BookStatus;
import ebookstore.repository.BookRequestRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.csv.writer.BookRequestCsvExporter;
import ebookstore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса для работы с запросами.
 * Управляет бизнес-логикой связанной с запросами, включая создание,
 * обновление, удаление и поиск запросов.
 */
@Component
public class BookRequestServiceImpl implements BookRequestService {

    @Autowired
    private BookRequestRepository requestRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BookRequestCsvExporter requestCsvExporter;

    private static final Logger log = LoggerFactory.getLogger(BookRequestServiceImpl.class);

    @Override
    public BookRequestDto createRequest(BookRequest request) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            clientService.checkClientIsExist(request.getClientId());
            Book book = bookService.getBookById(request.getBookId());

            if (book.getStatus() == BookStatus.AVAILABLE) {
                log.error("Попытка создать запрос на доступную книгу id={}", book.getId());
                throw new RuntimeException("Книга доступна, запрос не нужен");
            }

            request.setRequestStatus(BookRequestStatus.OPENED);
            BookRequest savedRequest = requestRepository.saveRequest(request);

            transaction.commit();
            return RequestMapper.mapRequestToDto(savedRequest);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при создании запроса: {}", request, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании запроса: {}", request, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка создания запроса", e);
        }
    }

    @Override
    public BookRequestDto update(BookRequest request) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            BookRequest existingRequest = requestRepository.getRequestById(request.getRequestId())
                    .orElseThrow(() -> {
                        log.error("Запрос с id={} не найден для обновления", request.getRequestId());
                        return new RequestNotFoundException(RequestErrorMessages.FIND_ERROR);
                    });

            updateRequestFields(existingRequest, request);
            BookRequest updatedRequest = requestRepository.updateRequest(existingRequest);

            transaction.commit();
            return RequestMapper.mapRequestToDto(updatedRequest);
        } catch (RequestNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при обновлении запроса: {}", request, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении запроса: {}", request, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка обновления запроса", e);
        }
    }

    @Override
    public BookRequestDto getRequestById(long requestId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            BookRequest request = requestRepository.getRequestById(requestId)
                    .orElseThrow(() -> {
                        log.error("Запрос не найден id={}", requestId);
                        return new RequestNotFoundException(RequestErrorMessages.FIND_ERROR);
                    });

            transaction.commit();
            return RequestMapper.mapRequestToDto(request);
        } catch (RequestNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении запроса с id={}", requestId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении запроса с id={}", requestId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения запроса", e);
        }
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            requestRepository.changeRequestStatus(requestId, status);
            transaction.commit();
            log.info("Статус запроса id={} изменён на {}", requestId, status);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при изменении статуса запроса с id={}", requestId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при изменении статуса запроса с id={}", requestId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка изменения статуса запроса", e);
        }
    }

    @Override
    public boolean requestIsOpenForBookWithId(long bookId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<BookRequest> allRequests = requestRepository.getAllRequests();
            transaction.commit();

            return allRequests.stream()
                    .anyMatch(r -> r.getBookId() == bookId &&
                                   r.getRequestStatus() == BookRequestStatus.OPENED);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при проверке открытых запросов для книги id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке открытых запросов для книги id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка проверки запросов", e);
        }
    }

    @Override
    public void closeRequestByBookId(long bookId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<BookRequest> allRequests = requestRepository.getAllRequests();

            allRequests.stream()
                    .filter(r -> r.getBookId() == bookId && r.getRequestStatus() == BookRequestStatus.OPENED)
                    .forEach(r -> {
                        r.setRequestStatus(BookRequestStatus.CLOSED);
                        requestRepository.updateRequest(r);
                    });

            transaction.commit();
            log.info("Закрыты все открытые запросы на книгу id={}", bookId);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при закрытии запросов для книги id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при закрытии запросов для книги id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка закрытия запросов", e);
        }
    }

    /**
     * работает через ж.. надо исправить, падает в ошибку, т.к. makeRequestDto создаётся ещё одна транзакция
     * как вариант получать здесь в транзакции все книги..короче надо не забыть поправить
     * <p>
     * Вынес метод сборки дто за транзакцию, теперь дто мапится за две транзакции
     * Вопрос - нужно ли это вообще?
     */
    @Override
    public Collection<RequestDto> getSortedRequest(Comparator<RequestDto> comparator) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            List<BookRequest> requests = new ArrayList<>(requestRepository.getAllRequests());

            transaction.commit();
            List<RequestDto> dtos = makeRequestDto(requests);

            dtos.sort(comparator);
            return dtos;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении запросов", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении запросов", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения запросов", e);
        }
    }

    @Override
    public boolean checkRequestIsExist(long requestId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            boolean result = requestRepository.checkRequestIsExist(requestId);
            transaction.commit();
            return result;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при проверке существования запроса с id={}", requestId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке существования запроса с id={}", requestId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка проверки существования запроса", e);
        }
    }

    @Override
    public void exportRequestsToCsv(String filePath) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<BookRequest> requests = requestRepository.getAllRequests();
            transaction.commit();

            requestCsvExporter.exportToCsv(requests, filePath);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при экспорте запросов в CSV", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при экспорте запросов в CSV", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка экспорта запросов", e);
        }
    }

    private List<RequestDto> makeRequestDto(List<BookRequest> bookRequests) {
        List<RequestDto> response = new ArrayList<>();
        Map<Long, Long> requestCounts = new HashMap<>();

        for (BookRequest br : bookRequests) {
            requestCounts.put(br.getBookId(),
                    requestCounts.getOrDefault(br.getBookId(), 0L) + 1);
        }

        for (BookRequest br : bookRequests) {
            Book book = bookService.getBookById(br.getBookId());
            response.add(
                    new RequestDto(br, requestCounts.get(br.getBookId()), book.getTitle())
            );
        }

        return response;
    }

    private void updateRequestFields(BookRequest existingRequest, BookRequest newData) {
        if (newData.getBookId() != 0) {
            existingRequest.setBookId(newData.getBookId());
        }
        if (newData.getClientId() != 0) {
            existingRequest.setClientId(newData.getClientId());
        }
        if (newData.getRequestStatus() != null) {
            existingRequest.setRequestStatus(newData.getRequestStatus());
        }
        if (newData.getCreatedOn() != null) {
            existingRequest.setCreatedOn(newData.getCreatedOn());
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                log.error("Ошибка при откате транзакции", rollbackEx);
            }
        }
    }
}