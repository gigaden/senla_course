package ebookstore.dto.book;

public record BookDescriptionDto(
        long id,
        String title,
        String author,
        String description
) {
}