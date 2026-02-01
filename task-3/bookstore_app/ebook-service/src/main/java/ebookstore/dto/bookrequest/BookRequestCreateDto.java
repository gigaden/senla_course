package ebookstore.dto.bookrequest;

import jakarta.validation.constraints.NotNull;

public record BookRequestCreateDto(@NotNull(message = "id книги должен быть заполнен")
                                   Long bookId,

                                   @NotNull(message = "id клиента должен быть заполнен")
                                   Long clientId) {
}
