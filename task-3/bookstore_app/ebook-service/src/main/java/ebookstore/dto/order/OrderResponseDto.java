package ebookstore.dto.order;

import java.time.LocalDate;

/**
 * Дто заказа для ответа
 */
public record OrderResponseDto(Long id,
                               Long bookId,
                               Long clientId,
                               LocalDate created,
                               LocalDate completed,
                               String status) {
}
