package ebookstore.repository.implement;

import di.annotation.Component;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.repository.BookRequestRepository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryBookRequestRepository implements BookRequestRepository, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<Long, BookRequest> requests;
    private long requestId;

    public InMemoryBookRequestRepository() {
        requests = new HashMap<>();
        requestId = 1;
    }

    @Override
    public Optional<BookRequest> getRequestById(long requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public Collection<BookRequest> getAllRequests() {
        return requests.values();
    }

    @Override
    public BookRequest saveRequest(BookRequest request) {
        long requestId = generateId();
        request.setRequestId(requestId);
        requests.put(requestId, request);

        return request;
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        BookRequest request = requests.get(requestId);
        request.setRequestStatus(status);
    }

    @Override
    public BookRequest updateRequest(BookRequest request) {
        BookRequest existingRequest = requests.get(request.getRequestId());
        if (existingRequest != null) {
            existingRequest.setBookId(request.getBookId());
            existingRequest.setClientId(request.getClientId());
            existingRequest.setRequestStatus(request.getRequestStatus());

            return existingRequest;
        }
        return null;
    }

    @Override
    public boolean checkRequestIsExist(long bookId) {
        return requests.containsKey(bookId);
    }

    private long generateId() {
        return requestId++;
    }
}