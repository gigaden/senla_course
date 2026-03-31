package ebookstore.service.implement;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.exception.notfound.RequestNotFoundException;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.enums.RequestSortField;
import ebookstore.repository.BookRequestRepository;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.csv.writer.BookRequestCsvExporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса запросов")
class BookRequestServiceImplTest {

    @InjectMocks
    private BookRequestServiceImpl requestService;

    @Mock
    private BookRequestRepository requestRepository;

    @Mock
    private BookService bookService;

    @Mock
    private ClientService clientService;

    @Mock
    private BookRequestCsvExporter csvExporter;

    @Test
    @DisplayName("Создание запроса успешно")
    void shouldCreateRequest() {
        BookRequestCreateDto dto = new BookRequestCreateDto(1L, 2L);

        Book book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.ABSENT);

        BookRequest request = new BookRequest(1L, 2L);
        request.setRequestId(10L);

        Mockito.when(bookService.getBookById(1L)).thenReturn(book);
        Mockito.when(requestRepository.saveRequest(Mockito.any())).thenReturn(request);

        BookRequestDto result = requestService.createRequest(dto);

        Assertions.assertEquals(10L, result.id());
        Mockito.verify(requestRepository).saveRequest(Mockito.any());
    }

    @Test
    @DisplayName("Ошибка: книга доступна")
    void shouldThrowWhenBookAvailable() {
        BookRequestCreateDto dto = new BookRequestCreateDto(1L, 2L);

        Book book = new Book();
        book.setId(1L);
        book.setStatus(BookStatus.AVAILABLE);

        Mockito.when(bookService.getBookById(1L)).thenReturn(book);

        Assertions.assertThrows(RuntimeException.class,
                () -> requestService.createRequest(dto));
    }

    @Test
    @DisplayName("Обновление запроса успешно")
    void shouldUpdateRequest() {
        BookRequest existing = new BookRequest(1L, 2L);
        existing.setRequestId(1L);

        BookRequest update = new BookRequest(3L, 4L);
        update.setRequestId(1L);

        Mockito.when(requestRepository.getRequestById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(requestRepository.updateRequest(Mockito.any())).thenReturn(existing);

        BookRequestDto result = requestService.update(update);

        Assertions.assertEquals(3L, result.bookId());
    }

    @Test
    @DisplayName("Ошибка обновления: не найден")
    void shouldThrowWhenUpdateNotFound() {
        BookRequest request = new BookRequest(1L, 2L);
        request.setRequestId(1L);

        Mockito.when(requestRepository.getRequestById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RequestNotFoundException.class,
                () -> requestService.update(request));
    }

    @Test
    @DisplayName("Получение запроса по id")
    void shouldGetById() {
        BookRequest request = new BookRequest(1L, 2L);
        request.setRequestId(1L);

        Mockito.when(requestRepository.getRequestById(1L)).thenReturn(Optional.of(request));

        BookRequestDto result = requestService.getRequestById(1L);

        Assertions.assertEquals(1L, result.id());
    }

    @Test
    @DisplayName("Ошибка: запрос не найден")
    void shouldThrowWhenNotFound() {
        Mockito.when(requestRepository.getRequestById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestById(1L));
    }

    @Test
    @DisplayName("Изменение статуса")
    void shouldChangeStatus() {
        requestService.changeRequestStatus(1L, BookRequestStatus.CLOSED);

        Mockito.verify(requestRepository).changeRequestStatus(1L, BookRequestStatus.CLOSED);
    }

    @Test
    @DisplayName("Проверка открытого запроса")
    void shouldCheckOpenRequest() {
        BookRequest request = new BookRequest(1L, 2L);
        request.setRequestStatus(BookRequestStatus.OPENED);

        Mockito.when(requestRepository.getAllRequests()).thenReturn(List.of(request));

        boolean result = requestService.requestIsOpenForBookWithId(1L);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Закрытие запросов")
    void shouldCloseRequests() {
        BookRequest request = new BookRequest(1L, 2L);
        request.setRequestStatus(BookRequestStatus.OPENED);

        Mockito.when(requestRepository.getAllRequests()).thenReturn(List.of(request));

        requestService.closeRequestByBookId(1L);

        Mockito.verify(requestRepository).updateRequest(Mockito.any());
    }

    @Test
    @DisplayName("Получение всех запросов")
    void shouldGetAll() {
        BookRequest request = new BookRequest(1L, 2L);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("title");

        Mockito.when(requestRepository.getAllRequests()).thenReturn(List.of(request));
        Mockito.when(bookService.getBookById(1L)).thenReturn(book);

        Collection<RequestDto> result =
                requestService.getAll(0, 5, RequestSortField.TITLE);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Проверка существования")
    void shouldCheckExist() {
        Mockito.when(requestRepository.checkRequestIsExist(1L)).thenReturn(true);

        boolean result = requestService.checkRequestIsExist(1L);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Экспорт в CSV")
    void shouldExport() {
        Mockito.when(requestRepository.getAllRequests()).thenReturn(List.of());

        requestService.exportRequestsToCsv("file.csv");

        Mockito.verify(csvExporter).exportToCsv(Mockito.any(), Mockito.eq("file.csv"));
    }
}