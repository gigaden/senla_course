package ebookstore.dto;

import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;

import java.time.LocalDate;

public record BookDescriptionDto(
        long id,
        String title,
        String author,
        String description,
        double price,
        BookStatus status,
        LocalDate dateOfPublication
) {
    public static BookDescriptionDto fromBook(Book book) {
        return new BookDescriptionDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getPrice(),
                book.getStatus(),
                book.getDateOfPublication()
        );
    }

    @Override
    public String toString() {
        return "BookDescriptionDto{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", status=" + status +
               ", dateOfPublication=" + dateOfPublication +
               '}';
    }
}