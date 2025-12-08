package ebookstore.service.implement;

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
import ebookstore.util.PropertiesUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BookServiceImpl implements BookService {

    private static final String MONTH_QUANTITY = "month_quantity";
    private static final String MARK_REQUEST_COMPLETED = "mark_request_completed";

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final BookCsvExporter bookCsvExporter;
    private final BookRequestService requestService;

    public BookServiceImpl(BookRepository bookRepository,
                           OrderRepository orderRepository,
                           BookRequestService requestService,
                           BookCsvExporter bookCsvExporter) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.requestService = requestService;
        this.bookCsvExporter = bookCsvExporter;
    }


    @Override
    public Book saveBook(Book book) {
        if (book.getId() == 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        Book newBook = bookRepository.saveBook(book);

        if (requestService.requestIsOpenForBookWithId(book.getId()) &&
            Boolean.parseBoolean(PropertiesUtil.get(MARK_REQUEST_COMPLETED))) {

            requestService.closeRequestByBookId(book.getId());
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
                .orElseThrow(() -> new BookNotFoundException(BookErrorMessages.FIND_ERROR));
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
    }

    @Override
    public Collection<Book> getStaleBooks(Comparator<Book> comparator) {
        List<Book> staleBooks = new ArrayList<>();
        Collection<Book> allBooks = bookRepository.getAllBooks();
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(Long.parseLong(PropertiesUtil.get(MONTH_QUANTITY)));

        for (Book book : allBooks) {
            boolean hasRecentOrder = false;
            Collection<Order> allOrders = orderRepository.getAllOrders();

            for (Order order : allOrders) {
                if (order.getBook().getId() == book.getId()
                    && order.getOrderStatus().equals(OrderStatus.COMPLETED)
                    && order.getCompletedOn() != null
                    && order.getCompletedOn().isAfter(sixMonthsAgo.toLocalDate())) {
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