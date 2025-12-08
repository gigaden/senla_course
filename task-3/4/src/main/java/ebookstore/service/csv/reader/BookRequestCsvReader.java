package ebookstore.service.csv.reader;

import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.service.BookRequestService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookRequestCsvReader {

    private final BookRequestService bookRequestService;

    public BookRequestCsvReader(BookRequestService bookRequestService) {
        this.bookRequestService = bookRequestService;
    }

    public List<List<String>> readFromCsv(String fileName) {
        List<List<String>> importRequests = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;
            while ((line = bf.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                List<String> lineData = Arrays.stream(values)
                        .map(String::trim)
                        .collect(Collectors.toList());
                importRequests.add(lineData);
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return importRequests;
    }

    public void saveBookRequestFromCsv(List<List<String>> requestsFromCsv) {
        int successCount = 0;
        int errorCount = 0;

        for (List<String> requestArr : requestsFromCsv) {
            try {
                if (requestArr.size() < 5) {
                    System.out.println("Пропущена строка: недостаточно данных - " + requestArr);
                    errorCount++;
                    continue;
                }

                long requestId = Long.parseLong(requestArr.get(0).trim());
                long bookId = Long.parseLong(requestArr.get(1).trim());
                long clientId = Long.parseLong(requestArr.get(2).trim());
                BookRequestStatus requestStatus = BookRequestStatus.valueOf(requestArr.get(3).trim());
                LocalDate createdOn = LocalDate.parse(requestArr.get(4).trim());

                BookRequest bookRequest = new BookRequest(bookId, clientId);
                bookRequest.setRequestId(requestId);
                bookRequest.setRequestStatus(requestStatus);
                bookRequest.setCreatedOn(createdOn);

                if (bookRequestService.checkRequestIsExist(requestId)) {
                    bookRequestService.update(bookRequest);
                    System.out.println("Обновлен запрос: " + requestId);
                } else {
                    bookRequestService.createRequest(bookRequest);
                    System.out.println("Добавлен запрос: " + requestId);
                }
                successCount++;

            } catch (Exception e) {
                System.out.println("Ошибка при обработке строки: " + requestArr + " - " + e.getMessage());
                errorCount++;
            }
        }

        System.out.printf("Импорт завершен. Успешно: %d, Ошибок: %d\n", successCount, errorCount);
    }
}