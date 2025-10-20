package ebookstore.controller;

import ebookstore.model.Book;
import ebookstore.service.BookService;

import java.util.Collection;

public class ConsoleBookController {

    private final BookService bookService;

    public ConsoleBookController(BookService bookService) {
        this.bookService = bookService;
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
}
