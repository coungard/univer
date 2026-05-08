package com.coungard.univer.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record RegisterStudentRequest(

        @NotBlank(message = "Логин обязателен")
        String username,

        @NotBlank(message = "Имя обязательно")
        String firstname,

        @NotBlank(message = "Фамилия обязательна")
        String lastname,

        String fullname,

        @Email(message = "Некорректный email")
        @NotBlank(message = "Email обязателен")
        String email,

        @NotNull(message = "Пароль обязателен")
        String password,

        @NotNull(message = "Дата зачисления обязательна")
        @PastOrPresent(message = "Дата зачисления не может быть в будущем")
        LocalDate enrollmentDate,

        @NotNull(message = "ID университета обязателен")
        UUID universityId
) {
}