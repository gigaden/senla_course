package ebookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Дто для обновления книги
 */
public record BookUpdateDto(@NotNull(message = "Укажите id книги")
                            Long id,
                            @NotBlank(message = "Название обязательно для заполнения")
                            @Size(min = 1, max = 256, message = "Название должно быть от 1 до 1024 символов")
                            String title,

                            @NotBlank(message = "Автор обязателен для заполнения")
                            @Size(min = 1, max = 256, message = "Имя автора должно быть от 1 до 300 символов")
                            String author,

                            @NotBlank(message = "Описание обязательно для заполнения")
                            @Size(min = 1, max = 2048, message = "Описание должно быть от 1 до 2048 символов")
                            String description,

                            @NotNull(message = "Дата должна быть указана в формате ГГГГ-ММ-ДД")
                            LocalDate date,

                            @NotNull(message = "Стоимость должна быть указана")
                            @Positive(message = "Стоимость должна быть положительной")
                            Double price) {
}
