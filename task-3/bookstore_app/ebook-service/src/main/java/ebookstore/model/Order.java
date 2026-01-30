package ebookstore.model;

import ebookstore.model.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "created_on")
    private LocalDate createdOn;
    @Column(name = "completed_on")
    private LocalDate completedOn;
    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(Book book, Client client) {
        this.book = book;
        this.client = client;
        createdOn = LocalDate.now();
        orderStatus = OrderStatus.NEW;
    }

    public Order() {

    }

    public long getOrderId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(LocalDate completedOn) {
        this.completedOn = completedOn;
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", book=" + book +
               ", client=" + client +
               ", createdOn=" + createdOn +
               ", completedOn=" + completedOn +
               ", orderStatus=" + orderStatus +
               '}';
    }
}
