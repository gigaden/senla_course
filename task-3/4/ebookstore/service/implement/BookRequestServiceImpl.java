package ebookstore.service.implement;

import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.BookStatus;
import ebookstore.repository.BookRequestRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;

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
        checkRequestIsExist(requestId);
        BookRequest request = requestRepository.getRequestById(requestId);

        return request;
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        BookRequest request = getRequestById(requestId);
        request.setRequestStatus(status);
    }

    @Override
    public boolean requestIsOpenForBookWithId(long bookId) {
        boolean flag = false;
        for (BookRequest request : requestRepository.getAllRequests().values()) {
            if (request.getBookId() == bookId && request.getRequestStatus().equals(BookRequestStatus.OPENED)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public void checkRequestIsExist(long requestId) {
        if (!requestRepository.getAllRequests().containsKey(requestId)) {
            System.out.printf("Запроса с id = %d не существует", requestId);
            throw new RuntimeException();
        }
    }
}
