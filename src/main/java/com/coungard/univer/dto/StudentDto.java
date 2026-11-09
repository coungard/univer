package com.coungard.univer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        UUID createdBy,
        UUID updatedBy,

        @Email(message = "Некорректный email")
        @NotBlank(message = "Email обязателен")
        String email,

        @PastOrPresent(message = "Дата зачисления не может быть в будущем")
        LocalDate enrollmentDate,

        @PastOrPresent(message = "Дата рождения не может быть в будущем")
        LocalDate birthday,

        UUID universityId,

        UUID facultyId,

        Integer yearNumber,

        UUID groupId,

        /** Фото профиля 512×512 — постоянная публичная ссылка; {@code null}, если фото нет (issue #137). Только ответ. */
        String avatarUrl,

        /** Превью 128×128 для списков (issue #137). Только ответ. */
        String avatarThumbnailUrl
) {
}