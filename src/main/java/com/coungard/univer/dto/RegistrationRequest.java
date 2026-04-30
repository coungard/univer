package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию нового пользователя")
public record RegistrationRequest(

        @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
        @Schema(description = "Имя пользователя", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
        @Schema(description = "Фамилия пользователя", example = "Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        @Schema(description = "Email пользователя", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 100, message = "Пароль должен быть не менее 6 символов")
        @Schema(description = "Пароль пользователя", example = "securepass123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}