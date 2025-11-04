package ebookstore.repository;

import ebookstore.model.Book;

import java.util.Collection;

public interface BookRepository {

    Collection<Book> getAllBooks();

    Book saveBook(Book book);

    Book getBook(long bookId);

    Book updateBook(Book book);

    void deleteBook(long bookId);

    boolean checkBookIsExist(long bookId);
}
