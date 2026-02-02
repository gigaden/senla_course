package ebookstore.dto.order;

import ebookstore.model.Book;
import ebookstore.model.Client;
import jakarta.validation.constraints.NotNull;

public record OrderCreateDto(@NotNull(message = "Книга должна быть добавлена")
                             Book book,

                             @NotNull(message = "Клиент должен быть добавлен")
                             Client client) {
}