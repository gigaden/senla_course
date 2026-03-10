package ebookstore.controller;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.dto.book.BookUpdateDto;
import ebookstore.model.enums.BookSortField;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;
import ebookstore.service.csv.reader.BookCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер обрабатывает запросы пользователя к книгам
 */
@Controller
public class ConsoleBookController {

    private final BookService bookService;
    private final BookCsvReader csvReader;
    private static final Logger log = LoggerFactory.getLogger(ConsoleBookController.class);

    public ConsoleBookController(BookService bookService, BookCsvReader csvReader) {
        this.bookService = bookService;
        this.csvReader = csvReader;
    }

    public void saveBook(BookCreateDto book) {
        log.info("Сохраняем в базу книгу {}", book.title());
        BookResponseDto responseDto = bookService.saveBook(book);
        System.out.println(responseDto);
        log.info("Сохранили в базу книгу {}", book.title());
    }

    public void getAllBooksByAlphabet() {
        log.info("Получаем все книги, отсортированные по алфавиту");
        Collection<BookResponseDto> books = bookService.getAllBooks(0, 10, BookSortField.TITLE);
        System.out.println(books);
        log.info("Получено книг по алфавиту: {}", books.size());
    }

    public void getAllBooksByDateOfPublish() {
        log.info("Получаем все книги, отсортированные по дате издания");
        Collection<BookResponseDto> books = bookService.getAllBooks(0, 10, BookSortField.DATE);
        log.info("Получено книг по дате издания: {}", books.size());
    }

    public void getAllBooksByPrice() {
        log.info("Получаем все книги, отсортированные по цене");
        Collection<BookResponseDto> books = bookService.getAllBooks(0, 10, BookSortField.PRICE);
        System.out.println(books);
        log.info("Получено книг по цене: {}", books.size());
    }

    public void getAllBooksByAvailability() {
        log.info("Получаем все книги, отсортированные по наличию на складе");
        Collection<BookResponseDto> books = bookService.getAllBooks(0, 10, BookSortField.STATUS);
        System.out.println(books);
        log.info("Получено книг по наличию на складе: {}", books.size());
    }

    public void getBook(long bookId) {
        log.info("Получаем книгу с id={}", bookId);
        BookResponseDto book = bookService.getBookDtoById(bookId);
        System.out.println(book);
        log.info("Получена книга с id={}", book.id());
    }

    public void updateBook(BookUpdateDto dto) {
        log.info("Обновляем книгу с id={}", dto.id());
        BookResponseDto response = bookService.updateBook(dto);
        System.out.println(response);
        log.info("Книга обновлена с id={}", dto.id());
    }

    public void deleteBook(long bookId) {
        log.info("Удаляем книгу с id={}", bookId);
        bookService.deleteBookById(bookId);
        log.info("Книга удалена с id={}", bookId);
    }

    public void changeBookStatusToAbsent(long bookId) {
        log.info("Меняем статус книги на ABSENT, id={}", bookId);
        bookService.changeBookStatus(bookId, BookStatus.ABSENT);
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
