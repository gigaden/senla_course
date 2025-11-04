package ebookstore.repository.implement;

import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.repository.BookRequestRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBookRequestRepository implements BookRequestRepository {

    private static InMemoryBookRequestRepository instance;

    private static Map<Long, BookRequest> requests;
    private static long requestId;

    private InMemoryBookRequestRepository() {
        requests = new HashMap<>();
        requestId = 0;
    }

    public static InMemoryBookRequestRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryBookRequestRepository();
        }

        return instance;
    }

    @Override
    public BookRequest getRequestById(long requestId) {
        BookRequest request = requests.get(requestId);

        return request;
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
    public boolean checkRequestIsExist(long bookId) {
        return requests.containsKey(bookId);
    }

    private long generateId() {
        long newId = requestId;
        requestId++;

        return newId;
    }
}
