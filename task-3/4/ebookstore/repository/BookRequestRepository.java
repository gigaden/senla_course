package ebookstore.repository;

import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;

import java.util.Map;

public interface BookRequestRepository {

    BookRequest getRequestById(long requestId);

    Map<Long, BookRequest> getAllRequests();

    BookRequest saveRequest(BookRequest request);

    void changeRequestStatus(long requestId, BookRequestStatus status);
}
