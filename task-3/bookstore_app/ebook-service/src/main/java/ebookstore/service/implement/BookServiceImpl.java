package ebookstore.service.implement;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.exception.notfound.BookNotFoundException;
import ebookstore.exception.message.BookErrorMessages;
import ebookstore.mapper.BookMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Реализация сервиса для работы с книгами.
 * Управляет бизнес-логикой связанной с книгами, включая создание,
 * обновление, удаление и поиск книг.
 */
@Service
@Validated
public class BookServiceImpl implements BookService {

    @Value("${month_quantity}")
    private Long monthQuantity;
    private static final long DEFAULT_MONTH_QUANTITY = 6L;
    @Value("${mark_request_completed}")
    private Boolean markRequestCompleted;

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final BookCsvExporter bookCsvExporter;
    private final BookRequestService requestService;

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    public BookServiceImpl(BookRepository bookRepository,
                           OrderRepository orderRepository,
                           BookCsvExporter bookCsvExporter,
                           @Lazy BookRequestService requestService) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.bookCsvExporter = bookCsvExporter;
        this.requestService = requestService;
    }

    @Override
    @Transactional
    public BookResponseDto saveBook(BookCreateDto bookDto) {
        Book book = BookMapper.mapCreateDtoToBook(bookDto);
        book.setStatus(BookStatus.AVAILABLE);
        log.debug("Установлен статус AVAILABLE для новой книги: {}", book.getTitle());

        Book newBook = bookRepository.saveBook(book);

        if (requestService.requestIsOpenForBookWithId(book.getId())
                && Boolean.TRUE.equals(markRequestCompleted)) {
            requestService.closeRequestByBookId(book.getId());
            log.info("Закрыт открытый запрос на книгу id={}", book.getId());
        }
        BookResponseDto response = BookMapper.mapBookToResponseDto(newBook);
        log.info("Сохранена книга {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Book> getAllBooks() {
        Collection<Book> books = bookRepository.getAllBooks();

        return List.copyOf(books);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookResponseDto> getAllBooks(Comparator<Book> comparator) {
        List<Book> books = new ArrayList<>(getAllBooks());
        books.sort(comparator);
        Collection<BookResponseDto> response = books.stream().map(BookMapper::mapBookToResponseDto).toList();
        log.info("Получены книги в количестве {}", response.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(long bookId) {
        Book book = bookRepository.getBook(bookId)
                .orElseThrow(() -> {
                    log.error("Книга с id={} не найдена", bookId);
                    return new BookNotFoundException(BookErrorMessages.FIND_ERROR);
                });
        log.info("Получена книга {}", book);

        return book;
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookDtoById(long bookId) {
        return BookMapper.mapBookToResponseDto(getBookById(bookId));
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Book book) {
        Book updatedBook = bookRepository.updateBook(book);
        BookResponseDto response = BookMapper.mapBookToResponseDto(updatedBook);
        log.info("Обновлена книга {}", response);

        return response;
    }

    @Override
    @Transactional
    public void deleteBookById(long bookId) {
        bookRepository.deleteBook(bookId);
        log.info("Книга с id = {} удалена", bookId);
    }

    @Override
    @Transactional
    public void makeBookAbsent(long bookId) {
        Book book = getBookById(bookId);
        book.setStatus(BookStatus.ABSENT);
        bookRepository.updateBook(book);

        log.info("Статус книги изменён на ABSENT, id={}", bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookResponseDto> getStaleBooks(Comparator<Book> comparator) {
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
        Collection<BookResponseDto> response = staleBooks.stream()
                .map(BookMapper::mapBookToResponseDto)
                .toList();
        log.info("Получены залежавшиеся книги");

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BookDescriptionDto getBookDescription(long bookId) {
        Book book = getBookById(bookId);
        log.info("Получено описание книги, id={}", bookId);

        return BookMapper.mapToBookDescriptionDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkBookIsExist(long bookId) {
        boolean result = bookRepository.checkBookIsExist(bookId);
        log.info("Результат проверки существования книги с id = {} {}", bookId, result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportBooksToCsv(String filePath) {
        Collection<Book> allBooks = getAllBooks();
        bookCsvExporter.exportToCsv(allBooks, filePath);
    }

    @Override
    @Transactional(readOnly = true)
    public void importBooksFromCsv(String filePath) {
        Collection<Book> allBooks = getAllBooks();
        bookCsvExporter.exportToCsv(allBooks, filePath);
    }
}