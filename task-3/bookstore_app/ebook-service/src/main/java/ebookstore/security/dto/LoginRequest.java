package ebookstore.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Дто для логин клиента
 */
public record LoginRequest(
        @NotBlank(message = "Логин пользователя не должен быть пустым")
        @Size(min = 3, max = 256, message = "Логин должен быть от 3 до 256 символов")
        String username,
        @NotBlank(message = "Пароль пользователя не должен быть пустым")
        @Size(min = 3, max = 256, message = "Пароль должен быть от 3 до 256 символов")
        String password) {
}