package ebookstore.service;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.RequestSortField;
import jakarta.validation.Valid;

import java.util.Collection;

public interface BookRequestService {

    BookRequestDto createRequest(@Valid BookRequestCreateDto request);

    BookRequestDto getRequestById(long requestId);

    void changeRequestStatus(long requestId, BookRequestStatus status);

    boolean requestIsOpenForBookWithId(long bookId);

    Collection<RequestDto> getAll(int page, int size, RequestSortField sortField);

    boolean checkRequestIsExist(long requestId);

    BookRequestDto update(BookRequest request);

    void closeRequestByBookId(long bookId);

    void exportRequestsToCsv(String filePath);
}
