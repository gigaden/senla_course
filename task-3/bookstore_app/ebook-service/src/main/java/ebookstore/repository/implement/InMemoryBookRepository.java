package ebookstore.repository.implement;

import di.annotation.Component;
import ebookstore.model.Book;
import ebookstore.repository.BookRepository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryBookRepository implements BookRepository, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<Long, Book> books;
    private long bookId;

    public InMemoryBookRepository() {
        books = new HashMap<>();
        bookId = 1;
    }

    @Override
    public Collection<Book> getAllBooks() {
        return books.values();
    }

    @Override
    public Book saveBook(Book book) {
        if (book.getId() == 0) {
            long newId = generateId();
            book.setId(newId);
        }
        books.put(book.getId(), book);
        return book;
    }

    @Override
    public Optional<Book> getBook(long bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    @Override
    public Book updateBook(Book book) {
        Book existingBook = books.get(book.getId());
        if (existingBook != null) {
            existingBook.setTitle(book.getTitle());
            existingBook.setAuthor(book.getAuthor());
            existingBook.setDescription(book.getDescription());
            existingBook.setDateOfPublication(book.getDateOfPublication());
            existingBook.setPrice(book.getPrice());
            existingBook.setStatus(book.getStatus());
            return existingBook;
        }
        return null;
    }

    @Override
    public void deleteBook(long bookId) {
        books.remove(bookId);
    }

    @Override
    public boolean checkBookIsExist(long bookId) {
        return books.containsKey(bookId);
    }

    private long generateId() {
        return bookId++;
    }
}