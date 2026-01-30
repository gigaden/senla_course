package ebookstore.dto.bookrequest;

import ebookstore.model.BookRequest;

public record RequestDto(BookRequest request, long requestCount, String bookTitle) {
}
