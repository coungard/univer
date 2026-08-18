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

/**
 * Студенческая группа. Название ({@code name}) — свободная человекочитаемая строка (например,
 * «У532 КСиТ»), не разбирается на части: источник истины — связь с {@link Semester} (а через неё
 * со {@link StudyYear} и {@code Program}), а не текст названия. См. TARGET.md, раздел
 * «Группа: соглашение об именовании».
 */
@Entity
@Table(name = "student_groups")
@NoArgsConstructor
@Data
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "semester_id", nullable = false)
  private Semester semester;

  @Column(name = "name", nullable = false, length = 64)
  private String name;
}
