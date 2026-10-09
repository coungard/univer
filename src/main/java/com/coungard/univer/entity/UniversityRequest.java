package com.coungard.univer.entity;

import com.coungard.univer.dto.UniversityRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Заявка студента на добавление университета, которого нет в справочнике. Сам студент университеты
 * не создаёт: заявку разбирает администратор — добавляет университет и закрывает заявку либо
 * отклоняет её.
 */
@Entity
@Table(name = "university_requests")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Data
public class UniversityRequest implements Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @Column(nullable = false)
  private String name;

  /**
   * Регион, выбранный студентом. Может быть пуст: студент мог не найти и регион — тогда регион
   * университета администратор указывает сам.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id")
  private Region region;

  /**
   * У студента не больше одной заявки в {@code PENDING} — см. частичный уникальный индекс в
   * {@code V39__create_university_requests.sql}.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 16)
  private UniversityRequestStatus status = UniversityRequestStatus.PENDING;

  /**
   * Университет, которым заявка закрыта. Заполнен только у {@code COMPLETED}.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "university_id")
  private University university;

  /**
   * Необязательное пояснение администратора при отклонении: что не так с заявкой.
   */
  @Column(length = 500)
  private String comment;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;
}
