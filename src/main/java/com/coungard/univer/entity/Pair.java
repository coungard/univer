package com.coungard.univer.entity;

import com.coungard.univer.dto.WeekParity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конкретный слот циклического расписания: день недели + время + чётность недели, на которой
 * действует. Связан с {@link Group} многие-ко-многим — лекция может идти потоком сразу нескольким
 * группам, а практика/семинар — тем же {@code Pair}, но с одной связанной группой (см. TARGET.md,
 * раздел «Поток: одна пара — несколько групп»).
 */
@Entity
@Table(name = "pairs")
@NoArgsConstructor
@Data
public class Pair {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "week_schedule_cycle_id", nullable = false)
  private WeekScheduleCycle weekScheduleCycle;

  @Column(name = "day_of_week", nullable = false, length = 16)
  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;

  @Column(name = "week_parity", nullable = false, length = 8)
  @Enumerated(EnumType.STRING)
  private WeekParity weekParity;

  @Column(name = "pair_number", nullable = false)
  private Integer pairNumber;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacher_id")
  private Teacher teacher;

  /**
   * Аудитория — свободная строка (например, "125"), не отдельная сущность (см. TARGET.md, где
   * {@code Room} задумана как полноценная связь {@code Pair }o--o| Room}). Опциональна.
   */
  @Column(name = "room", length = 32)
  private String room;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "pair_groups",
      joinColumns = @JoinColumn(name = "pair_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id")
  )
  private Set<Group> groups = new HashSet<>();
}
