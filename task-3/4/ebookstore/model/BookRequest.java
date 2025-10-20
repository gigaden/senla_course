package ebookstore.model;

import ebookstore.model.enums.BookRequestStatus;

import java.time.LocalDateTime;

public class BookRequest {

    private long requestId;

    private long bookId;

    private long clientId;

    private BookRequestStatus requestStatus;

    private final LocalDateTime createdOn;

    public BookRequest(long bookId, long clientId) {
        this.bookId = bookId;
        this.clientId = clientId;
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

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long userId) {
        this.clientId = userId;
    }

    public BookRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(BookRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return "BookRequest{" +
                "requestId=" + requestId +
                ", bookId=" + bookId +
                ", clientId=" + clientId +
                ", requestStatus=" + requestStatus +
                ", createdOn=" + createdOn +
                '}';
    }
}
