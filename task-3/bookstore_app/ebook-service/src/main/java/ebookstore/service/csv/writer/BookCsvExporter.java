package ebookstore.service.csv.writer;

import di.annotation.Component;
import ebookstore.model.Book;
import ebookstore.service.CsvExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component
public class BookCsvExporter implements CsvExporter<Book> {

    @Override
    public void exportToCsv(Collection<Book> books, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,title,author,description,dateOfPublication,price,status");

            for (Book book : books) {
                String line = String.format("%d,%s,%s,%s,%s,%.2f,%s",
                        book.getId(),
                        escapeCsv(book.getTitle()),
                        escapeCsv(book.getAuthor()),
                        escapeCsv(book.getDescription()),
                        book.getDateOfPublication(),
                        book.getPrice(),
                        book.getStatus()
                );
                writer.println(line);
            }

            System.out.println("Экспорт книг завершен. Файл: " + filePath);
            System.out.println("Экспортировано книг: " + books.size());

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте книг в CSV: " + e.getMessage());
            throw new RuntimeException("Ошибка экспорта", e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}