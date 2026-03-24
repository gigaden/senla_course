package ebookstore.controller.restapi;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.dto.book.BookUpdateDto;
import ebookstore.model.enums.BookSortField;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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
     * Эндпоинт для получения всех книг с сортировкой и пагинацией
     *
     * @param page - страница, с которой будем получать книги
     * @param size - количество записей
     * @param sort - по какому значению сортируем
     * @return - коллекция всех книг
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<Collection<BookResponseDto>> getAllBooks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sort", defaultValue = "TITLE") String sort) {

        log.info("Получаем книги page={}, size={}, sort={}",
                page, size, sort);

        BookSortField sortField = BookSortField.fromString(sort);
        Collection<BookResponseDto> books = bookService.getAllBooks(page, size, sortField);

        return ResponseEntity.ok(books);
    }

    /**
     * Эндпоинт для сохранения новой книги
     *
     * @param book - дто для новой книги
     * @return - сохранённая книга
     */
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBook(@PathVariable(name = "bookId") long bookId) {
        log.info("Получаем книгу с id={}", bookId);
        BookResponseDto book = bookService.getBookDtoById(bookId);

        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    /**
     * Эндпоинт для обновления книги
     *
     * @param dto - сущность книги для обновления
     * @return - обновлённая дто книги
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<BookResponseDto> updateBook(@RequestBody @Valid BookUpdateDto dto) {
        log.info("Обновляем книгу с id={}", dto.id());
        BookResponseDto response = bookService.updateBook(dto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для удаления книги
     *
     * @param bookId - id книги, которую нужно удалить
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable(name = "bookId") long bookId) {
        log.info("Удаляем книгу с id={}", bookId);
        bookService.deleteBookById(bookId);

        return new ResponseEntity<>("Книга удалена", HttpStatus.NO_CONTENT);
    }

    /**
     * Эндпоинт для изменения статуса книги
     *
     * @param bookId - id книги, которую нужно изменить
     * @param status - новый статус для книги
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{bookId}")
    public ResponseEntity<String> changeBookStatus(@PathVariable(name = "bookId") long bookId,
                                                   @RequestParam BookStatus status) {
        log.info("Меняем статус книги на {}, id={}", status, bookId);
        bookService.changeBookStatus(bookId, status);

        return new ResponseEntity<>(String.format("Статус книги %s изменён %s", bookId, status), HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения описания книги
     *
     * @param bookId - id книги, которую нужно изменить
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{bookId}/description")
    public ResponseEntity<BookDescriptionDto> getBookDescription(@PathVariable(name = "bookId") long bookId) {
        log.info("Получаем описание книги с id={}", bookId);
        BookDescriptionDto response = bookService.getBookDescription(bookId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения всех залежавшихся книг
     *
     * @return - коллекция всех книг
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stale")
    public ResponseEntity<Collection<BookResponseDto>> getStaleBooks() {
        log.info("Получаем залежавшиеся книги");
        Collection<BookResponseDto> books = bookService.getStaleBooks();

        return ResponseEntity.ok(books);
    }
}