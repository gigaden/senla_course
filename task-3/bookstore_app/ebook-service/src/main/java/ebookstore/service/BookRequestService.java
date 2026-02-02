package ebookstore.service;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.Comparator;

public interface BookRequestService {

    BookRequestDto createRequest(@Valid BookRequestCreateDto request);

    BookRequestDto getRequestById(long requestId);

    void changeRequestStatus(long requestId, BookRequestStatus status);

    boolean requestIsOpenForBookWithId(long bookId);

    Collection<RequestDto> getSortedRequest(Comparator<RequestDto> comparator);

    boolean checkRequestIsExist(long requestId);

    BookRequestDto update(BookRequest request);

    void closeRequestByBookId(long bookId);

    void exportRequestsToCsv(String filePath);
}
