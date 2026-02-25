package ebookstore.controller.restapi;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Comparator;

/**
 * Контроллер обрабатывает обращение к книгам
 */
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final static Logger log = LoggerFactory.getLogger(BookController.class);

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Эндпоинт для сохранения новой книги
     *
     * @param book - дто для новой книги
     * @return - сохранённая книга
     */
    @PostMapping
    public ResponseEntity<BookResponseDto> saveBook(@RequestBody @Valid BookCreateDto book) {
        log.info("Сохраняем в базу книгу {}", book.title());
        BookResponseDto response = bookService.saveBook(book);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для получения книги по id
     *
     * @param bookId - id для получения книги
     * @return - сохранённая книга
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBook(@PathVariable(name = "bookId") long bookId) {
        log.info("Получаем книгу с id={}", bookId);
        BookResponseDto book = bookService.getBookDtoById(bookId);

        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    /**
     * Эндпоинт для обновления книги
     *
     * @param book - сущность книги для обновления
     * @return - обновлённая дто книги
     */
    @PutMapping
    public ResponseEntity<BookResponseDto> updateBook(@RequestBody @Valid Book book) { // не забыть поменять на дто
        log.info("Обновляем книгу с id={}", book.getId());
        BookResponseDto response = bookService.updateBook(book);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для удаления книги
     *
     * @param bookId - id книги, которую нужно удалить
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable(name = "bookId") long bookId) {
        log.info("Удаляем книгу с id={}", bookId);
        bookService.deleteBookById(bookId);

        return new ResponseEntity<>("Книга удалена", HttpStatus.NO_CONTENT);
    }

    /**
     * Эндпоинт для изменения статуса книги на "отсутствует"
     *
     * @param bookId - id книги, которую нужно изменить
     */
    @PatchMapping("/{bookId}")
    public ResponseEntity<String> changeBookStatusToAbsent(@PathVariable(name = "bookId") long bookId) {
        log.info("Меняем статус книги на ABSENT, id={}", bookId);
        bookService.makeBookAbsent(bookId);

        return new ResponseEntity<>("Статус книги изменён", HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения описания книги
     *
     * @param bookId - id книги, которую нужно изменить
     */
    @GetMapping("/{bookId}/description")
    public ResponseEntity<BookDescriptionDto> getBookDescription(@PathVariable(name = "bookId") long bookId) {
        log.info("Получаем описание книги с id={}", bookId);
        BookDescriptionDto response = bookService.getBookDescription(bookId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения всех книг по алфавиту
     *
     * @return - коллекция всех книг по алфавиту
     */
    @GetMapping("/by_alphabet")
    public ResponseEntity<Collection<BookResponseDto>> getAllBooksByAlphabet() {
        log.info("Получаем все книги, отсортированные по алфавиту");
        Collection<BookResponseDto> books = bookService.getAllBooks(Comparator.comparing(Book::getTitle));

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения всех книг по дате публикации
     *
     * @return - коллекция всех книг по дате публикации
     */
    @GetMapping("/by_publish_date")
    public ResponseEntity<Collection<BookResponseDto>> getAllBooksByDateOfPublish() {
        log.info("Получаем все книги, отсортированные по дате издания");
        Collection<BookResponseDto> books = bookService.getAllBooks(Comparator.comparing(Book::getDateOfPublication));

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/by_price")
    public ResponseEntity<Collection<BookResponseDto>> getAllBooksByPrice() {
        log.info("Получаем все книги, отсортированные по цене");
        Collection<BookResponseDto> books = bookService.getAllBooks(Comparator.comparing(Book::getPrice));

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/by_availability")
    public ResponseEntity<Collection<BookResponseDto>> getAllBooksByAvailability() {
        log.info("Получаем все книги, отсортированные по наличию на складе");
        Collection<BookResponseDto> books = bookService.getAllBooks((o1, o2) -> {
            if (o1.getStatus() == BookStatus.AVAILABLE && o2.getStatus() != BookStatus.AVAILABLE) {
                return -1;
            } else if (o1.getStatus() != BookStatus.AVAILABLE && o2.getStatus() == BookStatus.AVAILABLE) {
                return 1;
            } else {
                return 0;
            }
        });

        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}