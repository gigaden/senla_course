package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.dto.bookrequest.RequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.service.BookRequestService;
import ebookstore.service.csv.reader.BookRequestCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Контроллер обрабатывает обращения к запросам на книги
 */
@Component
public class ConsoleBookRequestController {

    @Autowired
    private BookRequestService requestService;

    @Autowired
    private BookRequestCsvReader csvReader;

    private static final Logger log = LoggerFactory.getLogger(ConsoleBookRequestController.class);

    public ConsoleBookRequestController() {
    }

    public void createRequest(BookRequest request) {
        log.info("Создаём запрос на книгу");
        BookRequestDto response = requestService.createRequest(request);
        System.out.println(response);
        log.info("Запрос успешно создан");
    }

    public void changeRequestStatusToClosed(long requestId) {
        log.info("Закрываем запрос с id={}", requestId);
        requestService.changeRequestStatus(requestId, BookRequestStatus.CLOSED);
        log.info("Запрос закрыт с id={}", requestId);
    }

    public void getRequest(long requestId) {
        log.info("Получаем запрос с id={}", requestId);
        BookRequestDto response = requestService.getRequestById(requestId);
        System.out.println(response);
        log.info("Запрос получен с id={}", requestId);
    }

    public void getAllBookRequestByCountOfRequest() {
        log.info("Получаем запросы, отсортированные по количеству запросов");
        Collection<RequestDto> requests = requestService
                .getSortedRequest(Comparator.comparing(RequestDto::requestCount).reversed());
        System.out.println(requests);
        log.info("Получено запросов: {}", requests.size());
    }

    public void getAllBookRequestByTitleOfBookByAlphabet() {
        log.info("Получаем запросы, отсортированные по названию книги");
        Collection<RequestDto> requests = requestService
                .getSortedRequest(Comparator.comparing(RequestDto::bookTitle));
        System.out.println(requests);
        log.info("Получено сортированных запросов: {}", requests.size());
    }

    public void importRequestsFromCsv(String filePath) {
        log.info("Импортируем запросы из CSV файла {}", filePath);
        try {
            List<List<String>> requestsData = csvReader.readFromCsv(filePath);
            log.info("Найдено записей в CSV файле: {}", requestsData.size());
            csvReader.saveBookRequestFromCsv(requestsData);
            log.info("Импорт запросов завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при импорте запросов из CSV файла {}", filePath, e);
        }
    }

    public void exportRequestsToCsv(String filePath) {
        log.info("Экспортируем запросы в CSV файл {}", filePath);
        try {
            requestService.exportRequestsToCsv(filePath);
            log.info("Экспорт запросов завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при экспорте запросов в CSV файл {}", filePath, e);
        }
    }
}
