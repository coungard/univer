package com.coungard.univer.dto;

/**
 * Пол студента или преподавателя ({@code Person.gender}), issue #139. Необязателен: у пользователей,
 * зарегистрированных до его появления, и у тех, кто его не указал, — {@code null}.
 */
public enum Gender {
  MALE,
  FEMALE
}
