package ebookstore.repository;

import ebookstore.model.BookRequest;
import ebookstore.model.BookRequestStatus;

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

    private long generateId() {
        long newId = requestId;
        requestId++;

        return newId;
    }

    @Override
    public BookRequest getRequestById(long requestId) {
        return null;
    }

    @Override
    public Map<Long, BookRequest> getAllRequests() {
        return Map.of();
    }

    @Override
    public BookRequest saveRequest(BookRequest request) {
        return null;
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {

    }
}
