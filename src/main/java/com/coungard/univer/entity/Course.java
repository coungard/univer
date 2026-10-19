package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@Data
public class Course extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id")
  private Department department;

  /**
   * Прямая связь с факультетом — для предметов без кафедры, в том числе созданных студентами
   * (у факультета, созданного студентом, кафедр нет). У предмета с кафедрой может быть пустой:
   * тогда его факультет — факультет кафедры.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id")
  private Faculty faculty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacher_id")
  private Teacher teacher;

  /**
   * Студент, создавший предмет сам ({@code POST /students/me/course}); {@code null} у предметов,
   * заведённых администратором или преподавателем. По нему считается лимит предметов на студента.
   */
  @Column(name = "created_by_student_id")
  private UUID createdByStudentId;
}