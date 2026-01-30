package ebookstore.dto.order;

import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;

public record OrderDetailsDto(Order order, Client client, Book book) {
}