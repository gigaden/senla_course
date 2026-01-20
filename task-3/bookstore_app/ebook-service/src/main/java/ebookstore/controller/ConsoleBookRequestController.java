package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookRequestDto;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.service.BookRequestService;
import ebookstore.service.csv.reader.BookRequestCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

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
        requestService.createRequest(request);
        log.info("Запрос успешно создан");
    }

    public void changeRequestStatusToClosed(long requestId) {
        log.info("Закрываем запрос с id={}", requestId);
        requestService.changeRequestStatus(requestId, BookRequestStatus.CLOSED);
        log.info("Запрос закрыт с id={}", requestId);
    }

    public void getRequest(long requestId) {
        log.info("Получаем запрос с id={}", requestId);
        requestService.getRequestById(requestId);
        log.info("Запрос получен с id={}", requestId);
    }

    public void getAllBookRequestByCountOfRequest() {
        log.info("Получаем запросы, отсортированные по количеству запросов");
        Collection<BookRequestDto> requests = requestService
                .getSortedRequest(Comparator.comparing(BookRequestDto::getRequestCount).reversed());
        log.info("Получено запросов: {}", requests.size());
    }

    public void getAllBookRequestByTitleOfBookByAlphabet() {
        log.info("Получаем запросы, отсортированные по названию книги");
        Collection<BookRequestDto> requests = requestService
                .getSortedRequest(Comparator.comparing(BookRequestDto::getBookTitle));
        log.info("Получено запросов: {}", requests.size());
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
