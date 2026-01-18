package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookDescriptionDto;
import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;
import ebookstore.service.csv.reader.BookCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class ConsoleBookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCsvReader csvReader;

    private static final Logger log = LoggerFactory.getLogger(ConsoleBookController.class);

    public ConsoleBookController() {
    }

    public void saveBook(Book book) {
        log.info("Сохраняем в базу книгу {}", book.getTitle());
        bookService.saveBook(book);
        log.info("Сохранили в базу книгу {}", book.getTitle());
    }

    public void getAllBooks() {
        log.info("Получаем все книги");
        Collection<Book> books = bookService.getAllBooks();
        log.info("Получено книг: {}", books.size());
    }

    public void getAllBooksByAlphabet() {
        log.info("Получаем все книги, отсортированные по алфавиту");
        Collection<Book> books = bookService.getAllBooks(Comparator.comparing(Book::getTitle));
        log.info("Получено книг по алфавиту: {}", books.size());
    }

    public void getAllBooksByDateOfPublish() {
        log.info("Получаем все книги, отсортированные по дате издания");
        Collection<Book> books = bookService.getAllBooks(Comparator.comparing(Book::getDateOfPublication));
        log.info("Получено книг по дате издания: {}", books.size());
    }

    public void getAllBooksByPrice() {
        log.info("Получаем все книги, отсортированные по цене");
        Collection<Book> books = bookService.getAllBooks(Comparator.comparing(Book::getPrice));
        log.info("Получено книг по цене: {}", books.size());
    }

    public void getAllBooksByAvailability() {
        log.info("Получаем все книги, отсортированные по наличию на складе");
        Collection<Book> books = bookService.getAllBooks(new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                if (o1.getStatus() == BookStatus.AVAILABLE && o2.getStatus() != BookStatus.AVAILABLE) {
                    return -1;
                } else if (o1.getStatus() != BookStatus.AVAILABLE && o2.getStatus() == BookStatus.AVAILABLE) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        log.info("Получено книг по наличию на складе: {}", books.size());
    }

    public void getBook(long bookId) {
        log.info("Получаем книгу с id={}", bookId);
        Book book = bookService.getBookById(bookId);
        log.info("Получена книга с id={}", book.getId());
    }

    public void updateBook(Book book) {
        log.info("Обновляем книгу с id={}", book.getId());
        bookService.updateBook(book);
        log.info("Книга обновлена с id={}", book.getId());
    }

    public void deleteBook(long bookId) {
        log.info("Удаляем книгу с id={}", bookId);
        bookService.deleteBookById(bookId);
        log.info("Книга удалена с id={}", bookId);
    }

    public void changeBookStatusToAbsent(long bookId) {
        log.info("Меняем статус книги на ABSENT, id={}", bookId);
        bookService.makeBookAbsent(bookId);
        log.info("Статус книги изменён на ABSENT, id={}", bookId);
    }

    public void getBookDescription(long bookId) {
        log.info("Получаем описание книги с id={}", bookId);
        BookDescriptionDto bookDescription = bookService.getBookDescription(bookId);
        log.info("Получено описание книги с id={}", bookId);
    }

    public void importBooksFromCsv(String filePath) {
        log.info("Импортируем книги из CSV файла {}", filePath);
        try {
            List<List<String>> booksData = csvReader.readFromCsv(filePath);
            log.info("Найдено записей в CSV файле: {}", booksData.size());
            csvReader.saveBookFromCsv(booksData);
            log.info("Импорт книг из CSV завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при импорте книг из CSV файла {}", filePath, e);
        }
    }

    public void exportBooksToCsv(String filePath) {
        log.info("Экспортируем книги в CSV файл {}", filePath);
        try {
            bookService.exportBooksToCsv(filePath);
            log.info("Экспорт книг в CSV завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при экспорте книг в CSV файл {}", filePath, e);
        }
    }
}
