package ebookstore.dto.order;

import ebookstore.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Дто для обновления заказа
 */
public record OrderUpdateDto(@NotNull(message = "id должен быть указан")
                             Long id,
                             @NotNull(message = "id книги должен быть указан")
                             Long bookId,
                             @NotNull(message = "id клиента должен быть указан")
                             Long clientId,
                             @NotNull(message = "Статус заказа должен быт указан")
                             OrderStatus status) {
}