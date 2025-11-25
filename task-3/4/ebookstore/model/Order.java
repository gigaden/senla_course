package ebookstore.model;

import ebookstore.model.enums.OrderStatus;

import java.time.LocalDate;

public class Order {

    private long id;
    private Book book;
    private Client client;
    private LocalDate createdOn;
    private LocalDate completedOn;
    private OrderStatus orderStatus;

    public Order(Book book, Client client) {
        this.book = book;
        this.client = client;
        createdOn = LocalDate.now();
        orderStatus = OrderStatus.NEW;
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
