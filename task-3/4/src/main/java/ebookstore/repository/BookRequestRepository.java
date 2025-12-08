package ebookstore.repository;

import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;

import java.util.Collection;
import java.util.Optional;

public interface BookRequestRepository {

    Optional<BookRequest> getRequestById(long requestId);

    Collection<BookRequest> getAllRequests();

    BookRequest saveRequest(BookRequest request);

    void changeRequestStatus(long requestId, BookRequestStatus status);

    boolean checkRequestIsExist(long bookId);

    BookRequest updateRequest(BookRequest request);
}
