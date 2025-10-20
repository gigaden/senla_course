package ebookstore.service;

import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;

public interface BookRequestService {

    BookRequest createRequest(BookRequest request);

    BookRequest getRequestById(long requestId);

    void changeRequestStatus(long requestId, BookRequestStatus status);

    boolean requestIsOpenForBookWithId(long bookId);
}
