package ebookstore.service;

import ebookstore.dto.BookDescriptionDto;
import ebookstore.model.Book;

import java.util.Collection;
import java.util.Comparator;

public interface BookService {

    Book saveBook(Book book);

    Collection<Book> getAllBooks();

    Collection<Book> getAllBooks(Comparator<Book> comparator);

    Book getBookById(long bookId);

    Book updateBook(Book book);

    void deleteBookById(long bookId);

    void makeBookAbsent(long bookId);

    public BookDescriptionDto getBookDescription(long bookId);

    Collection<Book> getStaleBooks(Comparator<Book> comparator);
}
