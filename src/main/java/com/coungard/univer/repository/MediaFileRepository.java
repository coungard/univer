package com.coungard.univer.repository;

import com.coungard.univer.dto.MediaFileStatus;
import com.coungard.univer.entity.MediaFile;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {

  /** Какие из этих id есть в БД — для уборки файлов без записи ({@code MediaSweeper}). */
  @Query("select m.id from MediaFile m where m.id in :ids")
  Set<UUID> findExistingIds(Collection<UUID> ids);

  List<MediaFile> findByStatusAndCreatedAtBefore(MediaFileStatus status, Instant createdBefore);
}
