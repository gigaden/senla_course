package ebookstore.repository;

import ebookstore.model.Book;

import java.util.Collection;
import java.util.Optional;

public interface BookRepository {

    Collection<Book> getAllBooks();

    Collection<Book> getAllBooks(int page, int size, String sortBy);

    Book saveBook(Book book);

    Optional<Book> getBook(long bookId);

    Book updateBook(Book book);

    void deleteBook(long bookId);

    boolean checkBookIsExist(long bookId);
}
