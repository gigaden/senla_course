package ebookstore.controller;

import ebookstore.dto.BookRequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.service.BookRequestService;

import java.util.Collection;
import java.util.Comparator;

public class ConsoleBookRequestController {

    private final BookRequestService requestService;

    public ConsoleBookRequestController(BookRequestService requestService) {
        this.requestService = requestService;
    }

    public void createRequest(BookRequest request) {
        System.out.println("Сохраняем запрос в базу");
        BookRequest savedRequest = requestService.createRequest(request);
        System.out.printf("Сохранили запрос: %s\n", savedRequest);
    }

    public void changeRequestStatusToClosed(long requestId) {
        System.out.println("Закрываем запрос");
        requestService.changeRequestStatus(requestId, BookRequestStatus.CLOSED);
        BookRequest request = requestService.getRequestById(requestId);
        System.out.printf("Закрыли запрос: %s\n", request);
    }

    public void getRequest(long requestId) {
        System.out.printf("Получаем запрос с id = %d\n", requestId);
        BookRequest request = requestService.getRequestById(requestId);
        System.out.printf("Получили запрос: %s\n", request);
    }

    public void getAllBookRequestByCountOfRequest() {
        System.out.println("Получаем все запросы, отсортированные по количеству запросов");
        Collection<BookRequestDto> bookRequestDtos = requestService
                .getSortedRequest(Comparator.comparing(BookRequestDto::getRequestCount).reversed());
        System.out.printf("Получили все запросы по количеству запросов: %s\n", bookRequestDtos);
    }

    public void getAllBookRequestByTitleOfBookByAlphabet() {
        System.out.println("Получаем все запросы, отсортированные по алфавиту");
        Collection<BookRequestDto> bookRequestDtos = requestService
                .getSortedRequest(Comparator.comparing(BookRequestDto::getBookTitle));
        System.out.printf("Получили все запросы по алфавиту: %s\n", bookRequestDtos);
    }
}
