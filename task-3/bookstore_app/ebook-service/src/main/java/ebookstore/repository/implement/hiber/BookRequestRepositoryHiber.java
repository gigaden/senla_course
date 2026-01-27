package ebookstore.repository.implement.hiber;

import di.annotation.Component;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.repository.BookRequestRepository;

import java.util.Collection;
import java.util.Optional;

@Component
public class BookRequestRepositoryHiber extends BaseRepositoryHiber<BookRequest, Long> implements BookRequestRepository {

    public BookRequestRepositoryHiber() {
        super(BookRequest.class);
    }

    @Override
    public Optional<BookRequest> getRequestById(long requestId) {
        return Optional.ofNullable(find(requestId));
    }

    @Override
    public Collection<BookRequest> getAllRequests() {
        return findAll();
    }

    @Override
    public BookRequest saveRequest(BookRequest request) {
        return save(request);
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        BookRequest request = find(requestId);
        if (request != null) {
            request.setRequestStatus(status);
            update(request);
        }
    }

    @Override
    public boolean checkRequestIsExist(long requestId) {
        return exists(requestId);
    }

    @Override
    public BookRequest updateRequest(BookRequest request) {
        return update(request);
    }
}