package ebookstore.service.csv.writer;

import ebookstore.model.BookRequest;
import ebookstore.service.CsvExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class BookRequestCsvExporter implements CsvExporter<BookRequest> {

    @Override
    public void exportToCsv(Collection<BookRequest> bookRequests, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("requestId,bookId,clientId,requestStatus,createdOn");

            for (BookRequest request : bookRequests) {
                String line = String.format("%d,%d,%d,%s,%s",
                        request.getRequestId(),
                        request.getBookId(),
                        request.getClientId(),
                        request.getRequestStatus(),
                        request.getCreatedOn()
                );
                writer.println(line);
            }

            System.out.println("Экспорт запросов завершен. Файл: " + filePath);
            System.out.println("Экспортировано запросов: " + bookRequests.size());

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте запросов в CSV: " + e.getMessage());
            throw new RuntimeException("Ошибка экспорта", e);
        }
    }
}