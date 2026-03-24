package ebookstore.controller.restapi;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.model.enums.RequestSortField;
import ebookstore.service.BookRequestService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Контроллер обрабатывает запросы, связанные с запросами на книги
 */
@RestController
@RequestMapping("/requests")
public class BookRequestController {

    private final BookRequestService requestService;
    private static final Logger log = LoggerFactory.getLogger(BookRequestController.class);

    public BookRequestController(BookRequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * Эндпоинт для создания нового запроса на книгу
     *
     * @param request - дто для создания запроса
     * @return созданный запрос
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stale")
    public ResponseEntity<BookRequestDto> createRequest(@RequestBody @Valid BookRequestCreateDto request) {
        log.info("Создание запроса на книгу");
        BookRequestDto response = requestService.createRequest(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для получения запроса по его идентификатору
     *
     * @param id - идентификатор запроса
     * @return запрос
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BookRequestDto> getRequest(@PathVariable long id) {
        log.info("Получение запроса с id={}", id);
        BookRequestDto response = requestService.getRequestById(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для изменения статуса запроса
     *
     * @param id     - идентификатор запроса
     * @param status - новый статус
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeRequestStatus(@PathVariable long id,
                                                      @RequestParam BookRequestStatus status) {
        log.info("Изменение статуса запроса id={} на {}", id, status);
        requestService.changeRequestStatus(id, status);

        return new ResponseEntity<>("Статус запроса изменён", HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения всех запросов
     *
     * @return коллекция запросов
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Collection<RequestDto>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "TITLE") String sort) {
        log.info("Получение запросов page={}, size={}, sort={}", page, size, sort);
        RequestSortField sortField = RequestSortField.fromString(sort);
        Collection<RequestDto> requests = requestService
                .getAll(page, size, sortField);

        return new ResponseEntity<>(requests, HttpStatus.OK);
    }
}