package ebookstore.service.implement;

import ebookstore.dto.BookDescriptionDto;
import ebookstore.model.Book;
import ebookstore.model.Order;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.BookRepository;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    public BookServiceImpl(BookRepository bookRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Book saveBook(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        Book newBook = bookRepository.saveBook(book);

        return newBook;
    }

    @Override
    public Collection<Book> getAllBooks() {

        return List.copyOf(bookRepository.getAllBooks());
    }

    @Override
    public Collection<Book> getAllBooks(Comparator<Book> comparator) {
        List<Book> books = new ArrayList<>(bookRepository.getAllBooks());
        books.sort(comparator);

        return List.copyOf(books);
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

    @Override
    public Collection<Book> getStaleBooks(Comparator<Book> comparator) {
        List<Book> staleBooks = new ArrayList<>();
        Collection<Book> allBooks = bookRepository.getAllBooks();
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        for (Book book : allBooks) {
            boolean hasRecentOrder = false;
            Collection<Order> allOrders = orderRepository.getAllOrders();

            for (Order order : allOrders) {
                if (order.getBook().getId() == book.getId()
                    && order.getOrderStatus().equals(OrderStatus.COMPLETED)
                    && order.getCompletedOn() != null
                    && order.getCompletedOn().isAfter(sixMonthsAgo)) {
                    hasRecentOrder = true;
                    break;
                }
            }

            if (!hasRecentOrder) {
                staleBooks.add(book);
            }
        }

        staleBooks.sort(comparator);

        return List.copyOf(staleBooks);
    }

    @Override
    public BookDescriptionDto getBookDescription(long bookId) {
        checkBookIsExist(bookId);
        Book book = bookRepository.getBook(bookId);
        return BookDescriptionDto.fromBook(book);
    }

    private void checkBookIsExist(long bookId) {
        if (!bookRepository.checkBookIsExist(bookId)) {
            System.out.printf("Книги с id = %d не существует", bookId);
            throw new RuntimeException();
        }
    }
}
