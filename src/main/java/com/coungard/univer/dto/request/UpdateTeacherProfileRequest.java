package com.coungard.univer.dto.request;

import com.coungard.univer.dto.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

/**
 * Тело {@code PATCH /teachers/me}: частичное обновление профиля самим преподавателем (issue #139). Пока
 * только пол; он обязателен, поэтому явный {@code null} не очищает его, а даёт 422.
 *
 * <p>Класс с сеттерами, а не {@code record}, — как {@link UpdateStudentProfileRequest}: нужно отличать поле,
 * которого в запросе нет (не менять), от поля с явным {@code null} (очистить).
 */
@Getter
public class UpdateTeacherProfileRequest {

  private Gender gender;

  @JsonIgnore
  private boolean genderSet;

  public void setGender(Gender gender) {
    this.gender = gender;
    this.genderSet = true;
  }
}
