package com.coungard.univer.entity;

import com.coungard.univer.dto.MediaFileStatus;
import com.coungard.univer.dto.MediaPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Метаданные медиа-файла (issue #137). Сам файл — в {@code StorageService} по ключу {@link #storageKey}; URL
 * не хранится, его строит приложение.
 */
@Entity
@Table(name = "media_files")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class MediaFile extends AuditableEntity {

  /** Задаётся до записи файлов: он входит в их ключи. */
  @Id
  private UUID id;

  @PrePersist
  public void generateId() {
    if (id == null) {
      id = UUID.randomUUID();
    }
  }

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private MediaPurpose purpose;

  @Column(name = "storage_key", nullable = false, unique = true, length = 512)
  private String storageKey;

  @Column(name = "content_type", nullable = false, length = 100)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  @Column(name = "original_name")
  private String originalName;

  /** Subject JWT загрузившего. */
  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private MediaFileStatus status;
}
