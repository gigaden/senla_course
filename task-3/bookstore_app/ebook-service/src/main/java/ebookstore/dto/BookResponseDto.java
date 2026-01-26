package ebookstore.dto;

import ebookstore.model.enums.BookStatus;

import java.time.LocalDate;

public record BookResponseDto(Long id,
                              String title,
                              String author,
                              String description,
                              LocalDate publicationDate,
                              double price,
                              BookStatus status
                             ) {
}
