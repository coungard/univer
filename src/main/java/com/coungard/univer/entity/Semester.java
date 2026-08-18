package com.coungard.univer.entity;

import com.coungard.univer.dto.SemesterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "semesters")
@NoArgsConstructor
@Data
public class Semester {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_year_id", nullable = false)
  private StudyYear studyYear;

  @Column(name = "type", nullable = false, length = 16)
  @Enumerated(EnumType.STRING)
  private SemesterType type;

  /**
   * Дата начала семестра — от неё считается чётность недели циклического расписания
   * ({@code WeekScheduleCycle}), а не от календарной недели года.
   */
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;
}
