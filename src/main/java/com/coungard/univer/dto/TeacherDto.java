package com.coungard.univer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TeacherDto(
    UUID id,

    @NotBlank(message="Логин должен быть указан")
    String username,

    @NotBlank(message = "Имя обязательно")
    String firstname,

    @NotBlank(message = "Фамилия обязательна")
    String lastname,
    String fullname,

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    String email,

    Instant createdAt,
    Instant updatedAt,

    @NotNull(message = "ID кафедры обязателен")
    UUID departmentId,

    @NotBlank(message = "Должность должна быть заполнена")
    String position
) {

}