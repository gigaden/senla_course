package ebookstore.repository.implement.hiber;

import ebookstore.model.Book;
import ebookstore.repository.BookRepository;

import java.util.Collection;
import java.util.Optional;

public class BookRepositoryHiber extends BaseRepositoryHiber<Book, Long> implements BookRepository {

    public BookRepositoryHiber() {
        super(Book.class);
    }

    @Override
    public Collection<Book> getAllBooks() {
        return findAll();
    }

    @Override
    public Book saveBook(Book book) {
        return save(book);
    }

    @Override
    public Optional<Book> getBook(long bookId) {
        return Optional.ofNullable(find(bookId));
    }

    @Override
    public Book updateBook(Book book) {
        return update(book);
    }

    @Override
    public void deleteBook(long bookId) {
        delete(bookId);
    }

    @Override
    public boolean checkBookIsExist(long bookId) {
        return exists(bookId);
    }
}
