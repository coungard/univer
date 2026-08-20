package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lectures")
@NoArgsConstructor
@Data
public class Lecture {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "scheduled_time", nullable = false)
  private LocalDateTime scheduledTime;

  @Column(name = "duration_minutes", columnDefinition = "INT DEFAULT 90")
  private Integer durationMinutes;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacher_id")
  private Teacher teacher;

  /** Аудитория — свободная строка, при генерации из {@link Pair} копируется из {@code Pair.room}. */
  @Column(name = "room", length = 32)
  private String room;

  /**
   * Шаблон циклического расписания, из которого сгенерирована лекция (опционально — лекция может
   * быть создана и вручную). См. TARGET.md, раздел «Что это означает для будущих этапов».
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_pair_id")
  private Pair sourcePair;

  /**
   * Группы, для которых читается эта лекция — многие-ко-многим, как и у {@link Pair}: поточная
   * лекция ссылается сразу на несколько групп, а не дублируется по числу групп. См. TARGET.md,
   * раздел «Поток: одна пара — несколько групп».
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "lecture_groups",
      joinColumns = @JoinColumn(name = "lecture_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id")
  )
  private Set<Group> groups = new HashSet<>();
}
