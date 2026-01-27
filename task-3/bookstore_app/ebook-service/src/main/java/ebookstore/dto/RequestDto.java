package ebookstore.dto;

import ebookstore.model.BookRequest;

public class RequestDto {
    private final BookRequest request;
    private final long requestCount;
    private final String bookTitle;

    public RequestDto(BookRequest request, long requestCount, String bookTitle) {
        this.request = request;
        this.requestCount = requestCount;
        this.bookTitle = bookTitle;
    }

    public BookRequest getRequest() {
        return request;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    @Override
    public String toString() {
        return "BookRequestDto{" +
               "request=" + request +
               ", requestCount=" + requestCount +
               ", bookTitle='" + bookTitle + '\'' +
               '}';
    }
}
