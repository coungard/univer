package com.coungard.univer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Циклическое расписание семестра. Один цикл на семестр — чётность недели считается от
 * {@code Semester.startDate} и общая для всех групп этого семестра, поэтому цикл принадлежит
 * семестру, а не отдельной группе (см. TARGET.md, раздел «Поток: одна пара — несколько групп»).
 */
@Entity
@Table(name = "week_schedule_cycles")
@NoArgsConstructor
@Data
public class WeekScheduleCycle {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "semester_id", nullable = false, unique = true)
  private Semester semester;
}
