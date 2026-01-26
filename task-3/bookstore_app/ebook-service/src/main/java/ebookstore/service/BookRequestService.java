package ebookstore.service;

import ebookstore.dto.RequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;

import java.util.Collection;
import java.util.Comparator;

public interface BookRequestService {

    BookRequest createRequest(BookRequest request);

    BookRequest getRequestById(long requestId);

    void changeRequestStatus(long requestId, BookRequestStatus status);

    boolean requestIsOpenForBookWithId(long bookId);

    public Collection<RequestDto> getSortedRequest(Comparator<RequestDto> comparator);

    boolean checkRequestIsExist(long requestId);

    BookRequest update(BookRequest request);

    void closeRequestByBookId(long bookId);

    void exportRequestsToCsv(String filePath);
}
