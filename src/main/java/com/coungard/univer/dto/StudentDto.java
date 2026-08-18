package com.coungard.univer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record StudentDto(

        UUID id,

        @NotBlank(message = "Логин обязателен")
        String username,

        @NotBlank(message = "Имя обязательно")
        String firstname,

        @NotBlank(message = "Фамилия обязательна")
        String lastname,

        String fullname,
        Instant createdAt,
        Instant updatedAt,

        @Email(message = "Некорректный email")
        @NotBlank(message = "Email обязателен")
        String email,

        @NotNull(message = "Дата зачисления обязательна")
        @PastOrPresent(message = "Дата зачисления не может быть в будущем")
        LocalDate enrollmentDate,

        @NotNull(message = "ID университета обязателен")
        UUID universityId,

        UUID groupId
) {
}