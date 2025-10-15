package ebookstore.repository;

import ebookstore.model.Book;

import java.util.Map;

public interface BookRepository {

    Map<Long, Book> getAllBooks();

    Book saveBook(Book book);

    Book getBook(long bookId);

    Book updateBook(Book book);

    void deleteBook(long bookId);
}
