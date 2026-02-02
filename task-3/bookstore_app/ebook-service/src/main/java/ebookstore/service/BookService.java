package ebookstore.service;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.model.Book;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.Comparator;

public interface BookService {

    BookResponseDto saveBook(@Valid BookCreateDto book);

    Collection<Book> getAllBooks();

    Collection<BookResponseDto> getAllBooks(Comparator<Book> comparator);

    Book getBookById(long bookId);

    BookResponseDto getBookDtoById(long bookId);

    BookResponseDto updateBook(Book book);

    void deleteBookById(long bookId);

    void makeBookAbsent(long bookId);

    BookDescriptionDto getBookDescription(long bookId);

    Collection<BookResponseDto> getStaleBooks(Comparator<Book> comparator);

    boolean checkBookIsExist(long bookId);

    void exportBooksToCsv(String filePath);

    void importBooksFromCsv(String filePath);
}
