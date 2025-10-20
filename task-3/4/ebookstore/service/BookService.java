package ebookstore.service;

import ebookstore.model.Book;

import java.util.Collection;

public interface BookService {

    Book saveBook(Book book);

    Collection<Book> getAllBooks();

    Book getBookById(long bookId);

    Book updateBook(Book book);

    void deleteBookById(long bookId);

    void makeBookAbsent(long bookId);
}
