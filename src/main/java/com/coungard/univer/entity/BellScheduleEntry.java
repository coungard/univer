package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Звонковое расписание — справочник "номер пары -> время начала/окончания", per-university (см.
 * TARGET.md: сетка времён пар конфигурируема, у разных университетов может быть разное число пар
 * и разная длительность). {@code university == null} — системный дефолт, применяется, если у
 * конкретного {@link University} нет своей записи для этого {@code pairNumber}.
 *
 * <p>Это только источник для заполнения {@link Pair#getStartTime()}/{@link Pair#getEndTime()} при
 * создании пары, а не жёсткая связь — сами {@code Pair.startTime}/{@code endTime} остаются
 * самостоятельными редактируемыми колонками (см. {@code PairServiceImpl.resolveSchedule}).
 */
@Entity
@Table(name = "bell_schedule_entries")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Data
public class BellScheduleEntry implements Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "university_id")
  private University university;

  @Column(name = "pair_number", nullable = false)
  private Integer pairNumber;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}
