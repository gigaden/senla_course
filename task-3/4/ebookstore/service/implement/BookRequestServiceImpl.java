package ebookstore.service.implement;

import ebookstore.dto.BookRequestDto;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.BookStatus;
import ebookstore.repository.BookRequestRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRequestServiceImpl implements BookRequestService {

    private final BookRequestRepository requestRepository;
    private final BookService bookService;
    private final ClientService clientService;

    public BookRequestServiceImpl(BookRequestRepository requestRepository,
                                  BookService bookService,
                                  ClientService clientService) {
        this.requestRepository = requestRepository;
        this.bookService = bookService;
        this.clientService = clientService;
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
        BookRequest newRequest = requestRepository.saveRequest(request);

        return newRequest;
    }

    @Override
    public BookRequest getRequestById(long requestId) {
        return requestRepository.getRequestById(requestId).orElseThrow(() -> {
            System.out.printf("Запроса с id = %s не существует\n", requestId);
            return new RuntimeException();
        });
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

    /**
     * Не совсем понятная формулировка в тз "Список запросов на книгу (сортировать по количеству запросов)",
     * т.к. странно сортировать запросы по количеству запросов. Если бы нужно было сортировать книги по количеству
     * запросов на них, тогда понятно. В любом случае сделал как понял), но через запросы sql это было бы сделать проще.
     * Плюс появляются дубли, точнее почти одинаковые дто.
     */
    @Override
    public Collection<BookRequestDto> getSortedRequest(Comparator<BookRequestDto> comparator) {
        List<BookRequest> bookRequests = new ArrayList<>(requestRepository.getAllRequests());
        List<BookRequestDto> bookRequestDtos = makeRequestDto(bookRequests);
        bookRequestDtos.sort(comparator);

        return bookRequestDtos;
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

    public void checkRequestIsExist(long requestId) {
        if (!requestRepository.checkRequestIsExist(requestId)) {
            System.out.printf("Запроса с id = %d не существует", requestId);
            throw new RuntimeException();
        }
    }
}
