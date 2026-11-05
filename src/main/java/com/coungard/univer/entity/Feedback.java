package com.coungard.univer.entity;

import com.coungard.univer.dto.FeedbackPlatform;
import com.coungard.univer.dto.FeedbackStatus;
import com.coungard.univer.dto.FeedbackType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Отзыв из мобильного приложения: ошибка, предложение по доработке или идея (issue #130). Хранится только
 * в БД, никуда не пересылается; разбирает администратор, меняя статус.
 */
@Entity
@Table(name = "feedback")
@NoArgsConstructor
@Data
public class Feedback extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * Subject JWT автора. Ссылки на студента или преподавателя нет: отзыв может оставить и администратор.
   */
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /**
   * Старшая роль автора на момент отправки без префикса {@code ROLE_}: {@code ADMIN}, {@code TEACHER} или
   * {@code STUDENT}.
   */
  @Column(nullable = false, length = 16)
  private String role;

  /**
   * {@code preferred_username} из токена на момент отправки.
   */
  private String username;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private FeedbackType type;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "app_version", length = 32)
  private String appVersion;

  @Enumerated(EnumType.STRING)
  @Column(length = 16)
  private FeedbackPlatform platform;

  @Column(name = "os_version", length = 32)
  private String osVersion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private FeedbackStatus status = FeedbackStatus.NEW;
}
