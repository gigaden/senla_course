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

    public BookRequestServiceImpl() {
    }

    @Override
    public BookRequest createRequest(BookRequest request) {
        clientService.checkClientIsExist(request.getClientId());
        Book book = bookService.getBookById(request.getBookId());

        if (book.getStatus().equals(BookStatus.AVAILABLE)) {
            System.out.printf("Книга с id = %d доступна, нельзя оставить запрос на неё", book.getId());
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
                .orElseThrow(() -> new RequestNotFoundException(RequestErrorMessages.FIND_ERROR));
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        BookRequest request = getRequestById(requestId);
        request.setRequestStatus(status);
    }

    @Override
    public boolean requestIsOpenForBookWithId(long bookId) {
        return requestRepository.getAllRequests().stream()
                .anyMatch(r -> r.getBookId() == bookId &&
                        r.getRequestStatus().equals(BookRequestStatus.OPENED));
    }

    @Override
    public void closeRequestByBookId(long bookId) {
        requestRepository.getAllRequests().stream()
                .filter(r -> r.getBookId() == bookId && r.getRequestStatus() == BookRequestStatus.OPENED)
                .forEach(r -> r.setRequestStatus(BookRequestStatus.CLOSED));
    }

    @Override
    public Collection<BookRequestDto> getSortedRequest(Comparator<BookRequestDto> comparator) {
        List<BookRequest> bookRequests = new ArrayList<>(requestRepository.getAllRequests());
        List<BookRequestDto> bookRequestDtos = makeRequestDto(bookRequests);
        bookRequestDtos.sort(comparator);
        return bookRequestDtos;
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
            requestCounts.put(br.getBookId(), requestCounts.getOrDefault(br.getBookId(), 0L) + 1);
        }

        for (BookRequest br : bookRequests) {
            Book book = bookService.getBookById(br.getBookId());
            BookRequestDto bookRequestDto = new BookRequestDto(br, requestCounts.get(br.getBookId()), book.getTitle());
            response.add(bookRequestDto);
        }

        return response;
    }
}