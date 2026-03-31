package ebookstore.controller.restapi;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.dto.book.BookUpdateDto;
import ebookstore.exception.notfound.BookNotFoundException;
import ebookstore.model.enums.BookSortField;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты контроллера книг")
class BookControllerTest {

    @InjectMocks
    private BookController bookController;

    @Mock
    private BookService bookService;

    @Test
    @DisplayName("Получение всех книг")
    void shouldGetAllBooks() {
        BookResponseDto dto = new BookResponseDto(1L, "t", "a", "d",
                LocalDate.now(), 10.0, BookStatus.AVAILABLE);

        Mockito.when(bookService.getAllBooks(0, 5, BookSortField.TITLE))
                .thenReturn(List.of(dto));

        ResponseEntity<Collection<BookResponseDto>> response =
                bookController.getAllBooks(0, 5, "TITLE");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Создание книги")
    void shouldCreateBook() {
        BookCreateDto dto = new BookCreateDto("t", "a", "d",
                LocalDate.now(), 10.0);

        BookResponseDto responseDto = new BookResponseDto(1L, "t", "a", "d",
                LocalDate.now(), 10.0, BookStatus.AVAILABLE);

        Mockito.when(bookService.saveBook(dto)).thenReturn(responseDto);

        ResponseEntity<BookResponseDto> response = bookController.saveBook(dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("t", response.getBody().title());
    }

    @Test
    @DisplayName("Получение книги по id")
    void shouldGetBook() {
        BookResponseDto dto = new BookResponseDto(1L, "t", "a", "d",
                LocalDate.now(), 10.0, BookStatus.AVAILABLE);

        Mockito.when(bookService.getBookDtoById(1L)).thenReturn(dto);

        ResponseEntity<BookResponseDto> response = bookController.getBook(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Ошибка получения книги")
    void shouldThrowWhenGetBookFails() {
        Mockito.when(bookService.getBookDtoById(1L))
                .thenThrow(new BookNotFoundException("error"));

        Assertions.assertThrows(BookNotFoundException.class,
                () -> bookController.getBook(1L));
    }

    @Test
    @DisplayName("Обновление книги")
    void shouldUpdateBook() {
        BookUpdateDto dto = new BookUpdateDto(
                1L, "t", "a", "d", LocalDate.now(), 10.0
        );

        BookResponseDto responseDto = new BookResponseDto(1L, "t", "a", "d",
                LocalDate.now(), 10.0, BookStatus.AVAILABLE);

        Mockito.when(bookService.updateBook(dto)).thenReturn(responseDto);

        ResponseEntity<BookResponseDto> response = bookController.updateBook(dto);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Удаление книги")
    void shouldDeleteBook() {
        ResponseEntity<String> response = bookController.deleteBook(1L);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(bookService).deleteBookById(1L);
    }

    @Test
    @DisplayName("Изменение статуса")
    void shouldChangeStatus() {
        ResponseEntity<String> response =
                bookController.changeBookStatus(1L, BookStatus.AVAILABLE);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(bookService).changeBookStatus(1L, BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Получение описания книги")
    void shouldGetDescription() {
        BookDescriptionDto dto = new BookDescriptionDto(1L, "t", "a", "d");

        Mockito.when(bookService.getBookDescription(1L)).thenReturn(dto);

        ResponseEntity<BookDescriptionDto> response =
                bookController.getBookDescription(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Получение залежавшихся книг")
    void shouldGetStaleBooks() {
        Mockito.when(bookService.getStaleBooks()).thenReturn(List.of());

        ResponseEntity<Collection<BookResponseDto>> response =
                bookController.getStaleBooks();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}