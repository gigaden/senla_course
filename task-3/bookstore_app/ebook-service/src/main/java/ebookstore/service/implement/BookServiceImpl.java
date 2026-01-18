package ebookstore.service.implement;

import config.annotation.ConfigProperty;
import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookDescriptionDto;
import ebookstore.exception.BookNotFoundException;
import ebookstore.exception.message.BookErrorMessages;
import ebookstore.model.Book;
import ebookstore.model.Order;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.BookRepository;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.csv.writer.BookCsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class BookServiceImpl implements BookService {

    @ConfigProperty(configFileName = "application.properties", propertyName = "month_quantity", type = Long.class)
    private Long monthQuantity;

    private static final long DEFAULT_MONTH_QUANTITY = 6L;

    @ConfigProperty(configFileName = "application.properties", propertyName = "mark_request_completed", type = Boolean.class)
    private Boolean markRequestCompleted;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookCsvExporter bookCsvExporter;

    @Autowired
    private BookRequestService requestService;

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    @Override
    public Book saveBook(Book book) {
        if (book.getId() == 0) {
            book.setStatus(BookStatus.AVAILABLE);
            log.debug("Установлен статус AVAILABLE для новой книги");
        }

        Book newBook = bookRepository.saveBook(book);

        if (requestService.requestIsOpenForBookWithId(book.getId())
            && Boolean.TRUE.equals(markRequestCompleted)) {
            requestService.closeRequestByBookId(book.getId());
            log.info("Закрыт открытый запрос на книгу id={}", book.getId());
        }

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
        return bookRepository.getBook(bookId)
                .orElseThrow(() -> {
                    log.error("Книга с id={} не найдена", bookId);
                    return new BookNotFoundException(BookErrorMessages.FIND_ERROR);
                });
    }

    @Override
    public Book updateBook(Book book) {
        Book oldBook = getBookById(book.getId());
        return bookRepository.updateBook(oldBook);
    }

    @Override
    public void deleteBookById(long bookId) {
        Book book = getBookById(bookId);
        bookRepository.deleteBook(book.getId());
    }

    @Override
    public void makeBookAbsent(long bookId) {
        Book book = getBookById(bookId);
        book.setStatus(BookStatus.ABSENT);
        log.info("Статус книги изменён на ABSENT, id={}", bookId);
    }

    @Override
    public Collection<Book> getStaleBooks(Comparator<Book> comparator) {
        if (monthQuantity == null) {
            monthQuantity = DEFAULT_MONTH_QUANTITY;
            log.debug("monthQuantity не задан, используем значение по умолчанию = {}", DEFAULT_MONTH_QUANTITY);
        }

        LocalDateTime thresholdDate = LocalDateTime.now().minusMonths(monthQuantity);

        List<Book> staleBooks = new ArrayList<>();
        Collection<Book> allBooks = bookRepository.getAllBooks();
        Collection<Order> allOrders = orderRepository.getAllOrders();

        for (Book book : allBooks) {
            boolean hasRecentOrder = false;

            for (Order order : allOrders) {
                if (order.getBook().getId() == book.getId()
                    && order.getOrderStatus() == OrderStatus.COMPLETED
                    && order.getCompletedOn() != null
                    && order.getCompletedOn().isAfter(thresholdDate.toLocalDate())) {
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
        Book book = getBookById(bookId);
        return BookDescriptionDto.fromBook(book);
    }

    @Override
    public boolean checkBookIsExist(long bookId) {
        return bookRepository.checkBookIsExist(bookId);
    }

    @Override
    public void exportBooksToCsv(String filePath) {
        Collection<Book> allBooks = bookRepository.getAllBooks();
        bookCsvExporter.exportToCsv(allBooks, filePath);
    }

    @Override
    public void importBooksFromCsv(String filePath) {
        Collection<Book> allBooks = bookRepository.getAllBooks();
        bookCsvExporter.exportToCsv(allBooks, filePath);
    }
}