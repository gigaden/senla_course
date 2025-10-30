package ebookstore.dto;

import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;

public class OrderDetailsDto {
    private final Order order;
    private final Client client;
    private final Book book;

    public OrderDetailsDto(Order order, Client client, Book book) {
        this.order = order;
        this.client = client;
        this.book = book;
    }

    public Order getOrder() {
        return order;
    }

    public Client getClient() {
        return client;
    }

    public Book getBook() {
        return book;
    }

    @Override
    public String toString() {
        return "OrderDetailsDto{" +
               "order=" + order +
               ", client=" + client +
               ", book=" + book +
               '}';
    }
}