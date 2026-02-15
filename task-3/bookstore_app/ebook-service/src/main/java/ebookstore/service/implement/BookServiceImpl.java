package ebookstore.service.implement;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.exception.BookNotFoundException;
import ebookstore.exception.DatabaseException;
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
import ebookstore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
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
    private final HibernateUtil hibernateUtil;

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    public BookServiceImpl(BookRepository bookRepository,
                           OrderRepository orderRepository,
                           BookCsvExporter bookCsvExporter,
                           @Lazy BookRequestService requestService,
                           HibernateUtil hibernateUtil) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.bookCsvExporter = bookCsvExporter;
        this.requestService = requestService;
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public BookResponseDto saveBook(BookCreateDto bookDto) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Book book = BookMapper.mapCreateDtoToBook(bookDto);
            book.setStatus(BookStatus.AVAILABLE);
            log.debug("Установлен статус AVAILABLE для новой книги: {}", book.getTitle());

            Book newBook = bookRepository.saveBook(book);

            if (requestService.requestIsOpenForBookWithId(book.getId())
                && Boolean.TRUE.equals(markRequestCompleted)) {
                requestService.closeRequestByBookId(book.getId());
                log.info("Закрыт открытый запрос на книгу id={}", book.getId());
            }

            transaction.commit();
            return BookMapper.mapBookToResponseDto(newBook);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при сохранении книги: {}", bookDto, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении книги: {}", bookDto, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка сохранения книги", e);
        }
    }

    @Override
    public Collection<Book> getAllBooks() {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Book> books = bookRepository.getAllBooks();
            transaction.commit();

            return List.copyOf(books);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении всех книг", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении всех книг", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения книг", e);
        }
    }

    @Override
    public Collection<BookResponseDto> getAllBooks(Comparator<Book> comparator) {
        List<Book> books = new ArrayList<>(getAllBooks());
        books.sort(comparator);
        return books.stream().map(BookMapper::mapBookToResponseDto).toList();
    }

    @Override
    public Book getBookById(long bookId) {
        try {
            return bookRepository.getBook(bookId)
                    .orElseThrow(() -> {
                        log.error("Книга с id={} не найдена", bookId);
                        return new BookNotFoundException(BookErrorMessages.FIND_ERROR);
                    });
        } catch (BookNotFoundException e) {
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении книги с id={}", bookId, e);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении книги с id={}", bookId, e);
            throw new RuntimeException("Ошибка получения книги", e);
        }
    }

    @Override
    public BookResponseDto getBookDtoById(long bookId) {
        return BookMapper.mapBookToResponseDto(getBookById(bookId));
    }

    @Override
    public BookResponseDto updateBook(Book book) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Book updatedBook = bookRepository.updateBook(book);
            transaction.commit();
            return BookMapper.mapBookToResponseDto(updatedBook);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при обновлении книги: {}", book, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении книги: {}", book, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка обновления книги", e);
        }
    }

    @Override
    public void deleteBookById(long bookId) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            bookRepository.deleteBook(bookId);
            transaction.commit();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при удалении книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка удаления книги", e);
        }
    }

    @Override
    public void makeBookAbsent(long bookId) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Book book = bookRepository.getBook(bookId)
                    .orElseThrow(() -> {
                        log.error("Книга с id={} не найдена", bookId);
                        return new BookNotFoundException(BookErrorMessages.FIND_ERROR);
                    });

            book.setStatus(BookStatus.ABSENT);
            bookRepository.updateBook(book);

            transaction.commit();
            log.info("Статус книги изменён на ABSENT, id={}", bookId);
        } catch (BookNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при изменении статуса книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при изменении статуса книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка изменения статуса книги", e);
        }
    }

    @Override
    public Collection<BookResponseDto> getStaleBooks(Comparator<Book> comparator) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

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

            transaction.commit();
            staleBooks.sort(comparator);
            return staleBooks.stream()
                    .map(BookMapper::mapBookToResponseDto)
                    .toList();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении залежавшихся книг", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении залежавшихся книг", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка при получении залежавшихся книг", e);
        }
    }

    @Override
    public BookDescriptionDto getBookDescription(long bookId) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Book book = getBookById(bookId);

            transaction.commit();
            log.info("Получено описание книги, id={}", bookId);

            return BookMapper.mapToBookDescriptionDto(book);
        } catch (BookNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении описания книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении описания книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения описания книги", e);
        }
    }

    @Override
    public boolean checkBookIsExist(long bookId) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            boolean result = bookRepository.checkBookIsExist(bookId);
            transaction.commit();
            return result;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при проверке существования книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке существования книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка проверки существования книги", e);
        }
    }

    @Override
    public void exportBooksToCsv(String filePath) {
        Collection<Book> allBooks = getAllBooks();
        bookCsvExporter.exportToCsv(allBooks, filePath);
    }

    @Override
    public void importBooksFromCsv(String filePath) {
        Collection<Book> allBooks = getAllBooks();
        bookCsvExporter.exportToCsv(allBooks, filePath);
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                log.error("Ошибка при откате транзакции", rollbackEx);
            }
        }
    }
}