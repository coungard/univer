package com.coungard.univer.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.Getter;

/**
 * Тело {@code PATCH /students/me}: частичное обновление профиля самим студентом.
 *
 * <p>Класс с сеттерами, а не {@code record}, как остальные DTO: нужно отличать поле, которого в
 * запросе нет (не менять), от поля с явным {@code null} (очистить). Jackson вызывает сеттер только
 * для присутствующих в JSON полей — в том числе для {@code null}, — и сеттер это запоминает.
 */
@Getter
public class UpdateStudentProfileRequest {

  private UUID universityId;

  private UUID facultyId;

  @Min(value = 1, message = "Номер курса должен быть не меньше 1")
  @Max(value = 6, message = "Номер курса должен быть не больше 6")
  private Integer yearNumber;

  private UUID groupId;

  @JsonIgnore
  private boolean universityIdSet;

  @JsonIgnore
  private boolean facultyIdSet;

  @JsonIgnore
  private boolean yearNumberSet;

  @JsonIgnore
  private boolean groupIdSet;

  public void setUniversityId(UUID universityId) {
    this.universityId = universityId;
    this.universityIdSet = true;
  }

  public void setFacultyId(UUID facultyId) {
    this.facultyId = facultyId;
    this.facultyIdSet = true;
  }

  public void setYearNumber(Integer yearNumber) {
    this.yearNumber = yearNumber;
    this.yearNumberSet = true;
  }

  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
    this.groupIdSet = true;
  }
}
