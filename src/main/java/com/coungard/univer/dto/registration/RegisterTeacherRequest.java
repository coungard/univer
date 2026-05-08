package com.coungard.univer.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterTeacherRequest {

  @NotBlank(message = "Логин обязателен")
  String username;

  @NotBlank(message = "Имя обязательно")
  String firstname;

  @NotBlank(message = "Фамилия обязательна")
  String lastname;

  String fullname;

  @NotNull(message = "Пароль обязателен")
  String password;

  @Email(message = "Некорректный email")
  @NotBlank(message = "Email обязателен")
  String email;

  @NotNull(message = "ID кафедры обязателен")
  UUID departmentId;

  @NotBlank(message = "Должность должна быть указана")
  String position;
}