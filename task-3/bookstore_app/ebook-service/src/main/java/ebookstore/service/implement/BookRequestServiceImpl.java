package ebookstore.service.implement;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.exception.notfound.RequestNotFoundException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
@Service
@Validated
public class BookRequestServiceImpl implements BookRequestService {

    private final BookRequestRepository requestRepository;
    private final BookService bookService;
    private final ClientService clientService;
    private final BookRequestCsvExporter requestCsvExporter;
    private static final Logger log = LoggerFactory.getLogger(BookRequestServiceImpl.class);

    public BookRequestServiceImpl(BookRequestRepository requestRepository,
                                  BookService bookService,
                                  ClientService clientService,
                                  @Lazy BookRequestCsvExporter requestCsvExporter) {
        this.requestRepository = requestRepository;
        this.bookService = bookService;
        this.clientService = clientService;
        this.requestCsvExporter = requestCsvExporter;
    }

    @Override
    @Transactional
    public BookRequestDto createRequest(BookRequestCreateDto dto) {
        clientService.checkClientIsExist(dto.clientId());
        Book book = bookService.getBookById(dto.bookId());

        if (book.getStatus() == BookStatus.AVAILABLE) {
            log.error("Попытка создать запрос на доступную книгу id={}", book.getId());
            throw new RuntimeException("Книга доступна, запрос не нужен");
        }

        BookRequest request = RequestMapper.mapDtoToBookRequest(dto);
        request.setRequestStatus(BookRequestStatus.OPENED);
        BookRequest savedRequest = requestRepository.saveRequest(request);
        BookRequestDto response = RequestMapper.mapRequestToDto(savedRequest);
        log.info("Создан запрос {}", response);

        return response;
    }

    @Override
    @Transactional
    public BookRequestDto update(BookRequest request) {
        BookRequest existingRequest = requestRepository.getRequestById(request.getRequestId())
                .orElseThrow(() -> {
                    log.error("Запрос с id={} не найден для обновления", request.getRequestId());
                    return new RequestNotFoundException(RequestErrorMessages.FIND_ERROR);
                });

        updateRequestFields(existingRequest, request);
        BookRequest updatedRequest = requestRepository.updateRequest(existingRequest);
        BookRequestDto response = RequestMapper.mapRequestToDto(updatedRequest);
        log.info("Запрос обновлён {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BookRequestDto getRequestById(long requestId) {
        BookRequest request = requestRepository.getRequestById(requestId)
                .orElseThrow(() -> {
                    log.error("Запрос не найден id={}", requestId);
                    return new RequestNotFoundException(RequestErrorMessages.FIND_ERROR);
                });
        BookRequestDto response = RequestMapper.mapRequestToDto(request);
        log.info("Получен запрос {}", response);

        return response;
    }

    @Override
    @Transactional
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        requestRepository.changeRequestStatus(requestId, status);
        log.info("Статус запроса id={} изменён на {}", requestId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean requestIsOpenForBookWithId(long bookId) {
        Collection<BookRequest> allRequests = requestRepository.getAllRequests();

        return allRequests.stream()
                .anyMatch(r -> r.getBookId() == bookId &&
                        r.getRequestStatus() == BookRequestStatus.OPENED);
    }

    @Override
    @Transactional
    public void closeRequestByBookId(long bookId) {
        Collection<BookRequest> allRequests = requestRepository.getAllRequests();

        allRequests.stream()
                .filter(r -> r.getBookId() == bookId && r.getRequestStatus() == BookRequestStatus.OPENED)
                .forEach(r -> {
                    r.setRequestStatus(BookRequestStatus.CLOSED);
                    requestRepository.updateRequest(r);
                });

        log.info("Закрыты все открытые запросы на книгу id={}", bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<RequestDto> getSortedRequest(Comparator<RequestDto> comparator) {
        List<BookRequest> requests = new ArrayList<>(requestRepository.getAllRequests());
        List<RequestDto> dtos = makeRequestDto(requests);
        dtos.sort(comparator);
        log.info("Получены отсортированные запросы");

        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkRequestIsExist(long requestId) {
        boolean result = requestRepository.checkRequestIsExist(requestId);
        log.info("Проверка существования запроса с id {} = {}", requestId, result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportRequestsToCsv(String filePath) {
        Collection<BookRequest> requests = requestRepository.getAllRequests();
        requestCsvExporter.exportToCsv(requests, filePath);
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
}