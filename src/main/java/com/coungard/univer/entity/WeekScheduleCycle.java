package com.coungard.univer.entity;

import com.coungard.univer.dto.WeekScheduleCycleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  /**
   * Статус согласования. Новый цикл всегда создаётся в {@code DRAFT} (см.
   * {@code WeekScheduleCycleServiceImpl.createWeekScheduleCycle}) — поле-инициализатор здесь лишь
   * подстраховка для случаев создания сущности в обход сервиса. Пока {@code DRAFT} — {@code Pair}
   * цикла может править ADMIN или STUDENT своей группы; в {@code AGREED} — только ADMIN.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private WeekScheduleCycleStatus status = WeekScheduleCycleStatus.DRAFT;
}
