package ebookstore.service.csv.reader;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookCsvReader {

    @Autowired
    private BookService bookService;

    public BookCsvReader() {

    }

    public List<List<String>> readFromCsv(String fileName) {
        List<List<String>> importBooks = new ArrayList<>();

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
                importBooks.add(lineData);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return importBooks;
    }

    public void saveBookFromCsv(List<List<String>> booksFromCsv) {
        int successCount = 0;
        int errorCount = 0;

        for (List<String> bookArr : booksFromCsv) {
            try {
                if (bookArr.size() < 7) {
                    System.out.println("Пропущена строка: недостаточно данных - " + bookArr);
                    errorCount++;
                    continue;
                }

                long id = Long.parseLong(bookArr.get(0).trim());
                String title = bookArr.get(1).trim();
                String author = bookArr.get(2).trim();
                String description = bookArr.get(3).trim();
                LocalDate dateOfPublication = LocalDate.parse(bookArr.get(4).trim());
                double price = Double.parseDouble(bookArr.get(5).trim());
                BookStatus status = BookStatus.valueOf(bookArr.get(6).trim());

                Book book = new Book(title, author, description, dateOfPublication, price);
                book.setId(id);
                book.setStatus(status);

                if (bookService.checkBookIsExist(id)) {
                    bookService.updateBook(book);
                    System.out.println("Обновлена книга: " + title);
                } else {
                    bookService.saveBook(book);
                    System.out.println("Добавлена книга: " + title);
                }
                successCount++;
            } catch (Exception e) {
                System.out.println("Ошибка при обработке строки: " + bookArr + " - " + e.getMessage());
                errorCount++;
            }
        }

        System.out.printf("Импорт завершен. Успешно: %d, Ошибок: %d\n", successCount, errorCount);
    }
}