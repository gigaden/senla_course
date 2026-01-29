package ebookstore.mapper;

import ebookstore.dto.book.BookDescriptionDto;
import ebookstore.dto.book.BookResponseDto;
import ebookstore.model.Book;

/**
 * Маппер для книг
 */
public final class BookMapper {

    /**
     * Метод мапит из книги в дто
     *
     * @param book - книга
     * @return - дто
     */
    public static BookResponseDto mapBookToResponseDto(Book book) {
        return new BookResponseDto(book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getDateOfPublication(),
                book.getPrice(),
                book.getStatus());
    }

    /**
     * Метод мапит из книги в дто, у которого упор на описание, х.з. зачем, добавил, на всякий, другие поля
     * чтоб не чисто описание только было
     *
     * @param book - книга
     * @return - дто
     */
    public static BookDescriptionDto mapToBookDescriptionDto(Book book) {
        return new BookDescriptionDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription()
        );
    }
}
