package ebookstore.model;

import ebookstore.model.enums.BookRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "requests")
public class BookRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long requestId;

    @Column(name = "book_id")
    private long bookId;

    @Column(name = "client_id")
    private long clientId;

    @Column(name = "request_status")
    @Enumerated(EnumType.STRING)
    private BookRequestStatus requestStatus;

    @Column(name = "created_on")
    private LocalDate createdOn;

    public BookRequest(long bookId, long clientId) {
        this.bookId = bookId;
        this.clientId = clientId;
        createdOn = LocalDate.now();
    }

    public BookRequest() {

    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
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
