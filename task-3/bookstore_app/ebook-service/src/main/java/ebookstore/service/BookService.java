package ebookstore.service;

import ebookstore.dto.book.BookCreateDto;
import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.dto.book.BookUpdateDto;
import ebookstore.model.Book;
import ebookstore.model.enums.BookSortField;
import ebookstore.model.enums.BookStatus;
import jakarta.validation.Valid;

import java.util.Collection;

public interface BookService {

    BookResponseDto saveBook(@Valid BookCreateDto book);

    Collection<BookResponseDto> getAllBooks(int page, int size, BookSortField sortField);

    Book getBookById(long bookId);

    BookResponseDto getBookDtoById(long bookId);

    BookResponseDto updateBook(BookUpdateDto dto);

    void deleteBookById(long bookId);

    void changeBookStatus(long bookId, BookStatus status);

    BookDescriptionDto getBookDescription(long bookId);

    Collection<BookResponseDto> getStaleBooks();

    boolean checkBookIsExist(long bookId);

    void exportBooksToCsv(String filePath);

    void importBooksFromCsv(String filePath);
}
