package com.coungard.univer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record RegisterStudentDto(

        @NotBlank(message = "Логин обязателен")
        String username,

        @NotBlank(message = "Имя обязательно")
        String firstName,

        @NotBlank(message = "Фамилия обязательна")
        String lastName,

        String middleName,

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