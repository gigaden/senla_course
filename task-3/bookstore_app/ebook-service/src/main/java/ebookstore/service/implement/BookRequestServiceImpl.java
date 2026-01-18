package ebookstore.service.implement;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookRequestDto;
import ebookstore.exception.RequestNotFoundException;
import ebookstore.exception.message.RequestErrorMessages;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public BookRequest createRequest(BookRequest request) {
        clientService.checkClientIsExist(request.getClientId());
        Book book = bookService.getBookById(request.getBookId());

        if (book.getStatus() == BookStatus.AVAILABLE) {
            log.error("Попытка создать запрос на доступную книгу id={}", book.getId());
            throw new RuntimeException();
        }

        request.setRequestStatus(BookRequestStatus.OPENED);
        return requestRepository.saveRequest(request);
    }

    @Override
    public BookRequest update(BookRequest request) {
        BookRequest oldRequest = getRequestById(request.getRequestId());
        return requestRepository.updateRequest(oldRequest);
    }

    @Override
    public BookRequest getRequestById(long requestId) {
        return requestRepository.getRequestById(requestId)
                .orElseThrow(() -> {
                    log.error("Запрос не найден id={}", requestId);
                    return new RequestNotFoundException(RequestErrorMessages.FIND_ERROR);
                });
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        BookRequest request = getRequestById(requestId);
        request.setRequestStatus(status);
        log.info("Статус запроса id={} изменён на {}", requestId, status);
    }

    @Override
    public boolean requestIsOpenForBookWithId(long bookId) {
        return requestRepository.getAllRequests().stream()
                .anyMatch(r -> r.getBookId() == bookId &&
                               r.getRequestStatus() == BookRequestStatus.OPENED);
    }

    @Override
    public void closeRequestByBookId(long bookId) {
        requestRepository.getAllRequests().stream()
                .filter(r -> r.getBookId() == bookId && r.getRequestStatus() == BookRequestStatus.OPENED)
                .forEach(r -> r.setRequestStatus(BookRequestStatus.CLOSED));

        log.info("Закрыты все открытые запросы на книгу id={}", bookId);
    }

    @Override
    public Collection<BookRequestDto> getSortedRequest(Comparator<BookRequestDto> comparator) {
        List<BookRequest> requests = new ArrayList<>(requestRepository.getAllRequests());
        List<BookRequestDto> dtos = makeRequestDto(requests);
        dtos.sort(comparator);
        return dtos;
    }

    @Override
    public boolean checkRequestIsExist(long requestId) {
        return requestRepository.checkRequestIsExist(requestId);
    }

    @Override
    public void exportRequestsToCsv(String filePath) {
        Collection<BookRequest> allRequests = requestRepository.getAllRequests();
        requestCsvExporter.exportToCsv(allRequests, filePath);
    }

    private List<BookRequestDto> makeRequestDto(List<BookRequest> bookRequests) {
        List<BookRequestDto> response = new ArrayList<>();
        Map<Long, Long> requestCounts = new HashMap<>();

        for (BookRequest br : bookRequests) {
            requestCounts.put(br.getBookId(),
                    requestCounts.getOrDefault(br.getBookId(), 0L) + 1);
        }

        for (BookRequest br : bookRequests) {
            Book book = bookService.getBookById(br.getBookId());
            response.add(
                    new BookRequestDto(br, requestCounts.get(br.getBookId()), book.getTitle())
            );
        }

        return response;
    }
}