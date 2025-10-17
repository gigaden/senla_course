package ebookstore.service.implement;

import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;
import ebookstore.repository.BookRepository;
import ebookstore.service.BookService;

import java.util.Collection;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book saveBook(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        Book newBook = bookRepository.saveBook(book);

        return newBook;
    }

    @Override
    public Collection<Book> getAllBooks() {

        return bookRepository.getAllBooks().values();
    }

    @Override
    public Book getBookById(long bookId) {
        checkBookIsExist(bookId);

        return bookRepository.getBook(bookId);
    }

    @Override
    public Book updateBook(Book book) {
        checkBookIsExist(book.getId());
        Book newBook = bookRepository.updateBook(book);

        return newBook;
    }

    @Override
    public void deleteBookById(long bookId) {
        checkBookIsExist(bookId);
        bookRepository.deleteBook(bookId);
    }

    @Override
    public void makeBookAbsent(long bookId) {
        Book book = getBookById(bookId);
        book.setStatus(BookStatus.ABSENT);

    }

    private void checkBookIsExist(long bookId) {
        if (!bookRepository.getAllBooks().containsKey(bookId)) {
            System.out.printf("Книги с id = %d не существует", bookId);
            throw new RuntimeException();
        }
    }
}
