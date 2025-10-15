package ebookstore.model;

import java.time.LocalDateTime;

public class BookRequest {

    private long requestId;

    private long bookId;

    private long userId;

    private BookRequestStatus requestStatus;

    private final LocalDateTime createdOn;

    public BookRequest(long bookId, long userId, BookRequestStatus requestStatus) {
        this.bookId = bookId;
        this.userId = userId;
        this.requestStatus = requestStatus;
        createdOn = LocalDateTime.now();
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BookRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(BookRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
}
