package ebookstore.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientCreateDto(@NotBlank @Size(min = 5, max = 256, message = "Имя должно быть от 5 до 256 символов")
                              String name,

                              @NotBlank @Size(min = 5, max = 256, message = "Фамилия должна быть от 5 до 256 символов")
                              String surname,

                              @Email @Size(min = 5, max = 512, message = "email должен быть от 5 до 512 символов")
                              String email,

                              @NotBlank @Size(min = 5, max = 512, message = "Логин должен быть от 5 до 512 символов")
                              String login,

                              @NotBlank @Size(min = 5, max = 512, message = "Пароль должен быть от 5 до 512 символов")
                              String password) {
}
