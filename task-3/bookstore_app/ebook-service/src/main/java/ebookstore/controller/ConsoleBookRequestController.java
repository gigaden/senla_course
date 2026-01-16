package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookRequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.service.BookRequestService;
import ebookstore.service.csv.reader.BookRequestCsvReader;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class ConsoleBookRequestController {

    @Autowired
    private BookRequestService requestService;

    @Autowired
    private BookRequestCsvReader csvReader;

    public ConsoleBookRequestController() {
        // Конструктор без параметров для DI
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

    public void importRequestsFromCsv(String filePath) {
        System.out.println("Импортируем запросы из файла: " + filePath);
        try {
            List<List<String>> booksData = csvReader.readFromCsv(filePath);
            System.out.println("Найдено записей в файле: " + booksData.size());
            csvReader.saveBookRequestFromCsv(booksData);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте запросов: " + e.getMessage());
        }
    }

    public void exportRequestsToCsv(String filePath) {
        System.out.println("Экспортируем запросы в CSV файл: " + filePath);
        try {
            requestService.exportRequestsToCsv(filePath);
            System.out.println("Экспорт запросов успешно завершен!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте запросов: " + e.getMessage());
        }
    }
}