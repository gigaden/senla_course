package ebookstore.service.implement;

import config.annotation.ConfigProperty;
import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookDescriptionDto;
import ebookstore.exception.BookNotFoundException;
import ebookstore.exception.DatabaseException;
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
import ebookstore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            if (book.getId() == 0) {
                book.setStatus(BookStatus.AVAILABLE);
                log.debug("Установлен статус AVAILABLE для новой книги: {}", book.getTitle());
            }

            Book newBook = bookRepository.saveBook(book);

            if (requestService.requestIsOpenForBookWithId(book.getId())
                && Boolean.TRUE.equals(markRequestCompleted)) {
                requestService.closeRequestByBookId(book.getId());
                log.info("Закрыт открытый запрос на книгу id={}", book.getId());
            }

            transaction.commit();
            return newBook;

        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при сохранении книги: {}", book, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении книги: {}", book, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка сохранения книги", e);
        }
    }

    @Override
    public Collection<Book> getAllBooks() {
        Session session = HibernateUtil.getCurrentSession();
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
    public Collection<Book> getAllBooks(Comparator<Book> comparator) {
        List<Book> books = new ArrayList<>(getAllBooks());
        books.sort(comparator);
        return List.copyOf(books);
    }

    @Override
    public Book getBookById(long bookId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Book book = bookRepository.getBook(bookId)
                    .orElseThrow(() -> {
                        log.error("Книга с id={} не найдена", bookId);
                        return new BookNotFoundException(BookErrorMessages.FIND_ERROR);
                    });

            transaction.commit();
            return book;

        } catch (BookNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении книги с id={}", bookId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения книги", e);
        }
    }

    @Override
    public Book updateBook(Book book) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Book updatedBook = bookRepository.updateBook(book);
            transaction.commit();
            return updatedBook;

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
        Session session = HibernateUtil.getCurrentSession();
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
        Session session = HibernateUtil.getCurrentSession();
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
    public Collection<Book> getStaleBooks(Comparator<Book> comparator) {
        Session session = HibernateUtil.getCurrentSession();
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
            return List.copyOf(staleBooks);

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
        Book book = getBookById(bookId);
        return BookDescriptionDto.fromBook(book);
    }

    @Override
    public boolean checkBookIsExist(long bookId) {
        Session session = HibernateUtil.getCurrentSession();
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