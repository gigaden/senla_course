package ebookstore.dto.bookrequest;

import ebookstore.model.enums.BookRequestStatus;

import java.time.LocalDate;

public record BookRequestDto(long id,
                             long bookId,
                             long clientId,
                             BookRequestStatus status,
                             LocalDate created) {
}
