package ebookstore.mapper;

import ebookstore.dto.BookResponseDto;
import ebookstore.model.Book;

public final class BookMapper {

    public static BookResponseDto mapBookToResponseDto(Book book) {
        return new BookResponseDto(book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getDateOfPublication(),
                book.getPrice(),
                book.getStatus());
    }
}
