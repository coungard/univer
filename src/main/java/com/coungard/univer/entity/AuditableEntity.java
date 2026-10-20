package com.coungard.univer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Кто и когда создал и последним изменил запись (issue #112). Заполняет Spring Data JPA auditing:
 * автор — subject JWT текущего запроса (см. {@code CurrentActor}), без аутентификации — пусто.
 * Наследуйте от этого класса любую изменяемую сущность.
 *
 * <p>{@code created_*} не обновляются: иначе {@code save} сущности, собранной заново из DTO, затёр бы
 * их пустыми значениями.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditableEntity {

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private UUID createdBy;

  @LastModifiedBy
  @Column(name = "updated_by")
  private UUID updatedBy;

  /**
   * Из двух записей одного объекта — изменённая последней. Например, у студента имя и email лежат в
   * {@code Person}, и их правка не меняет {@code updated_at} самого студента (issue #119).
   */
  public static AuditableEntity lastModified(AuditableEntity first, AuditableEntity second) {
    if (second == null || second.getUpdatedAt() == null) {
      return first;
    }
    if (first.getUpdatedAt() == null || second.getUpdatedAt().isAfter(first.getUpdatedAt())) {
      return second;
    }
    return first;
  }
}
