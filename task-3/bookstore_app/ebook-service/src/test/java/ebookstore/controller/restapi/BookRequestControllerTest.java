package ebookstore.controller.restapi;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.exception.notfound.RequestNotFoundException;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.RequestSortField;
import ebookstore.service.BookRequestService;
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
@DisplayName("Тесты контроллера запросов")
class BookRequestControllerTest {

    @InjectMocks
    private BookRequestController controller;

    @Mock
    private BookRequestService requestService;

    @Test
    @DisplayName("Создание запроса")
    void shouldCreateRequest() {
        BookRequestCreateDto dto = new BookRequestCreateDto(1L, 2L);

        BookRequestDto responseDto =
                new BookRequestDto(1L, 1L, 2L, BookRequestStatus.OPENED, LocalDate.now());

        Mockito.when(requestService.createRequest(dto)).thenReturn(responseDto);

        ResponseEntity<BookRequestDto> response = controller.createRequest(dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(1L, response.getBody().id());
    }

    @Test
    @DisplayName("Ошибка создания запроса")
    void shouldThrowWhenCreateFails() {
        BookRequestCreateDto dto = new BookRequestCreateDto(1L, 2L);

        Mockito.when(requestService.createRequest(dto))
                .thenThrow(new RuntimeException("error"));

        Assertions.assertThrows(RuntimeException.class,
                () -> controller.createRequest(dto));
    }

    @Test
    @DisplayName("Получение запроса по id")
    void shouldGetRequest() {
        BookRequestDto dto =
                new BookRequestDto(1L, 1L, 2L, BookRequestStatus.OPENED, LocalDate.now());

        Mockito.when(requestService.getRequestById(1L)).thenReturn(dto);

        ResponseEntity<BookRequestDto> response = controller.getRequest(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1L, response.getBody().id());
    }

    @Test
    @DisplayName("Ошибка получения запроса")
    void shouldThrowWhenGetFails() {
        Mockito.when(requestService.getRequestById(1L))
                .thenThrow(new RequestNotFoundException("error"));

        Assertions.assertThrows(RequestNotFoundException.class,
                () -> controller.getRequest(1L));
    }

    @Test
    @DisplayName("Изменение статуса запроса")
    void shouldChangeStatus() {
        ResponseEntity<String> response =
                controller.changeRequestStatus(1L, BookRequestStatus.CLOSED);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(requestService)
                .changeRequestStatus(1L, BookRequestStatus.CLOSED);
    }

    @Test
    @DisplayName("Получение всех запросов")
    void shouldGetAll() {
        BookRequest request = new BookRequest(1L, 2L);

        RequestDto dto = new RequestDto(request, 1L, "title");

        Mockito.when(requestService.getAll(0, 5, RequestSortField.TITLE))
                .thenReturn(List.of(dto));

        ResponseEntity<Collection<RequestDto>> response =
                controller.getAllRequests(0, 5, "TITLE");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1, response.getBody().size());
    }
}