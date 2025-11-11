package ebookstore.repository.implement;

import ebookstore.model.Book;
import ebookstore.repository.BookRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookRepository implements BookRepository {

    private static InMemoryBookRepository instance;

    private static Map<Long, Book> books;
    private static long bookId;

    private InMemoryBookRepository() {
        books = new HashMap<>();
        bookId = 0;
    }

    public static InMemoryBookRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryBookRepository();
        }

        return instance;
    }

    public Collection<Book> getAllBooks() {
        return books.values();
    }

    @Override
    public Book saveBook(Book book) {
        long bookId = generateId();
        book.setId(bookId);
        books.put(bookId, book);

        return book;
    }

    @Override
    public Optional<Book> getBook(long bookId) {

        return Optional.ofNullable(books.get(bookId));
    }

    @Override
    public Book updateBook(Book book) {
        Book oldBook = books.get(book.getId());

        Book newBook = setNewBooksField(oldBook, book);

        return newBook;
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
        long newId = bookId;
        bookId++;

        return newId;
    }

    private Book setNewBooksField(Book oldBook, Book book) {
        oldBook.setAuthor(book.getAuthor());
        oldBook.setTitle(book.getTitle());
        oldBook.setDescription(book.getDescription());

        return oldBook;
    }
}
