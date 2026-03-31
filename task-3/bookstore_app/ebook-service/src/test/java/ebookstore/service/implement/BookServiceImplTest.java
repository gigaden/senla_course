package ebookstore.service.implement;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.dto.book.BookUpdateDto;
import ebookstore.exception.notfound.BookNotFoundException;
import ebookstore.mapper.BookMapper;
import ebookstore.model.Book;
import ebookstore.model.enums.BookSortField;
import ebookstore.model.enums.BookStatus;
import ebookstore.repository.BookRepository;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.csv.writer.BookCsvExporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса книг")
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BookCsvExporter bookCsvExporter;

    @Mock
    private BookRequestService requestService;

    @Test
    @DisplayName("Сохранение книги успешно")
    void shouldSaveBook() {
        BookCreateDto dto = new BookCreateDto(
                "title", "author", "desc",
                LocalDate.now(), 10.0
        );

        Book savedBook = BookMapper.mapCreateDtoToBook(dto);
        savedBook.setId(1L);

        Mockito.when(bookRepository.saveBook(any(Book.class)))
                .thenReturn(savedBook);

        Mockito.when(requestService.requestIsOpenForBookWithId(Mockito.anyLong()))
                .thenReturn(false);

        BookResponseDto response = bookService.saveBook(dto);

        Assertions.assertEquals("title", response.title());

        Mockito.verify(bookRepository).saveBook(any(Book.class));
    }

    @Test
    @DisplayName("Получение всех книг")
    void shouldReturnAllBooks() {
        Book book = new Book("t", "a", "d", LocalDate.now(), 10.0);

        Mockito.when(bookRepository.getAllBooks(0, 5, "title"))
                .thenReturn(List.of(book));

        Collection<BookResponseDto> result =
                bookService.getAllBooks(0, 5, BookSortField.TITLE);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Получение книги по id")
    void shouldReturnBookById() {
        Book book = new Book("t", "a", "d", LocalDate.now(), 10.0);
        book.setId(1L);

        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Ошибка: книга не найдена")
    void shouldThrowWhenBookNotFound() {
        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BookNotFoundException.class,
                () -> bookService.getBookById(1L));
    }

    @Test
    @DisplayName("Получение DTO книги")
    void shouldReturnBookDto() {
        Book book = new Book("t", "a", "d", LocalDate.now(), 10.0);
        book.setId(1L);

        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.of(book));

        BookResponseDto result = bookService.getBookDtoById(1L);

        Assertions.assertEquals("t", result.title());
    }

    @Test
    @DisplayName("Обновление книги успешно")
    void shouldUpdateBook() {
        Book book = new Book("old", "a", "d", LocalDate.now(), 10.0);
        book.setId(1L);

        BookUpdateDto dto = new BookUpdateDto(
                1L, "new", "a", "d", LocalDate.now(), 20.0
        );

        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.of(book));
        Mockito.when(bookRepository.updateBook(any())).thenReturn(book);

        BookResponseDto result = bookService.updateBook(dto);

        Assertions.assertEquals("new", result.title());
    }

    @Test
    @DisplayName("Ошибка при обновлении")
    void shouldThrowWhenUpdateNotFound() {
        BookUpdateDto dto = new BookUpdateDto(
                1L, "t", "a", "d", LocalDate.now(), 10.0
        );

        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(BookNotFoundException.class,
                () -> bookService.updateBook(dto));
    }

    @Test
    @DisplayName("Удаление книги")
    void shouldDeleteBook() {
        bookService.deleteBookById(1L);

        Mockito.verify(bookRepository).deleteBook(1L);
    }

    @Test
    @DisplayName("Изменение статуса книги")
    void shouldChangeStatus() {
        Book book = new Book("t", "a", "d", LocalDate.now(), 10.0);
        book.setId(1L);

        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.of(book));

        bookService.changeBookStatus(1L, BookStatus.AVAILABLE);

        Mockito.verify(bookRepository).updateBook(book);
    }

    @Test
    @DisplayName("Получение описания книги")
    void shouldReturnDescription() {
        Book book = new Book("t", "a", "d", LocalDate.now(), 10.0);
        book.setId(1L);

        Mockito.when(bookRepository.getBook(1L)).thenReturn(Optional.of(book));

        BookDescriptionDto result = bookService.getBookDescription(1L);

        Assertions.assertEquals("t", result.title());
    }

    @Test
    @DisplayName("Проверка существования книги")
    void shouldCheckExist() {
        Mockito.when(bookRepository.checkBookIsExist(1L)).thenReturn(true);

        boolean result = bookService.checkBookIsExist(1L);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Экспорт книг в CSV")
    void shouldExportBooks() {
        Mockito.when(bookRepository.getAllBooks()).thenReturn(List.of());

        bookService.exportBooksToCsv("file.csv");

        Mockito.verify(bookCsvExporter).exportToCsv(any(), Mockito.eq("file.csv"));
    }
}