package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.BookDescriptionDto;
import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;
import ebookstore.service.BookService;
import ebookstore.service.csv.reader.BookCsvReader;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class ConsoleBookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCsvReader csvReader;

    public ConsoleBookController() {
        // Конструктор без параметров для DI
    }

    public void saveBook(Book book) {
        System.out.println("Сохраняем книгу в базу");
        Book savedBook = bookService.saveBook(book);
        System.out.printf("Сохранили книгу: %s\n", savedBook);
    }

    public void getAllBooks() {
        System.out.println("Получаем все книги");
        Collection<Book> books = bookService.getAllBooks();
        System.out.printf("Получили все книги: %s\n", books);
    }

    public void getAllBooksByAlphabet() {
        System.out.println("Получаем все книги, отсортированные по алфавиту");
        Collection<Book> books = bookService.getAllBooks(Comparator.comparing(Book::getTitle));
        System.out.printf("Получили все книги по алфавиту: %s\n", books);
    }

    public void getAllBooksByDateOfPublish() {
        System.out.println("Получаем все книги, отсортированные по дате издания");
        Collection<Book> books = bookService.getAllBooks(Comparator.comparing(Book::getDateOfPublication));
        System.out.printf("Получили все книги по дате издания: %s\n", books);
    }

    public void getAllBooksByPrice() {
        System.out.println("Получаем все книги, отсортированные по цене");
        Collection<Book> books = bookService.getAllBooks(Comparator.comparing(Book::getPrice));
        System.out.printf("Получили все книги по цене: %s\n", books);
    }

    public void getAllBooksByAvailability() {
        System.out.println("Получаем все книги, отсортированные по наличию на складе");
        Collection<Book> books = bookService.getAllBooks(new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                if (o1.getStatus() == BookStatus.AVAILABLE && o2.getStatus() != BookStatus.AVAILABLE) {
                    return -1;
                } else if (o1.getStatus() != BookStatus.AVAILABLE && o2.getStatus() == BookStatus.AVAILABLE) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        System.out.printf("Получили все книги по наличию на складе: %s\n", books);
    }

    public void getBook(long bookId) {
        System.out.printf("Получаем книгу с id = %d\n", bookId);
        Book book = bookService.getBookById(bookId);
        System.out.printf("Получили книгу: %s\n", book);
    }

    public void updateBook(Book book) {
        System.out.printf("Обновляем книгу с id = %d\n", book.getId());
        Book newBook = bookService.updateBook(book);
        System.out.printf("Обновили книгу: %s\n", newBook);
    }

    public void deleteBook(long bookId) {
        System.out.printf("Удаляем книгу с id = %d\n", bookId);
        bookService.deleteBookById(bookId);
        System.out.printf("Удалили книгу: %s\n", bookId);
    }

    public void changeBookStatusToAbsent(long bookId) {
        System.out.printf("Меняем статус книги с id = %d на отсутствует\n", bookId);
        bookService.makeBookAbsent(bookId);
        System.out.printf("Изменили статус книги: %s\n", bookId);
    }

    public void getBookDescription(long bookId) {
        System.out.printf("Получаем описание книги с id = %d%n", bookId);
        BookDescriptionDto bookDescription = bookService.getBookDescription(bookId);
        System.out.printf("Получили описание книги: %s\n", bookDescription);
    }

    public void importBooksFromCsv(String filePath) {
        System.out.println("Импортируем книги из файла: " + filePath);
        try {
            List<List<String>> booksData = csvReader.readFromCsv(filePath);
            System.out.println("Найдено записей в файле: " + booksData.size());
            csvReader.saveBookFromCsv(booksData);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте книг: " + e.getMessage());
        }
    }

    public void exportBooksToCsv(String filePath) {
        System.out.println("Экспортируем книги в CSV файл: " + filePath);
        try {
            bookService.exportBooksToCsv(filePath);
            System.out.println("Экспорт книг успешно завершен!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте книг: " + e.getMessage());
        }
    }
}