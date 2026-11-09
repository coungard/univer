package com.coungard.univer.service;

import com.coungard.univer.dto.MediaFileStatus;
import com.coungard.univer.entity.MediaFile;
import com.coungard.univer.repository.MediaFileRepository;
import com.coungard.univer.service.StorageService.StoredObject;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Ежедневная уборка медиа-файлов (issue #137) — страховка от сбоев между хранилищем и БД, которые не делят
 * транзакцию:
 * <ul>
 *   <li>файлы аватаров, на которые нет строки в {@code media_files} (процесс упал между записью файла и
 *   коммитом, не удалось удалить файл после коммита). Свежие не трогаются: загрузка пишет файлы до коммита;</li>
 *   <li>записи {@code PENDING} старше суток вместе с файлами — незавершённые загрузки.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MediaSweeper {

  /** Файл моложе этого может принадлежать загрузке, транзакция которой ещё не закоммичена. */
  static final Duration ORPHAN_GRACE = Duration.ofHours(1);
  static final Duration PENDING_TTL = Duration.ofDays(1);
  private static final int ID_BATCH = 500;

  private final StorageService storageService;
  private final MediaFileRepository mediaFileRepository;
  private final MediaFileCleanup mediaFileCleanup;
  private final TransactionTemplate transactionTemplate;

  @Scheduled(cron = "${media.sweeper-cron}")
  public void sweep() {
    Instant now = Instant.now();
    int orphans = deleteOrphanAvatarFiles(now);
    int pending = deleteStalePending(now);
    log.info("Уборка медиа-файлов: удалено файлов-сирот {}, незавершённых загрузок {}", orphans, pending);
  }

  /** @return сколько файлов удалено */
  public int deleteOrphanAvatarFiles(Instant now) {
    Instant threshold = now.minus(ORPHAN_GRACE);
    Map<UUID, List<StoredObject>> byFileId = storageService.list(AvatarKeys.PREFIX).stream()
        .filter(object -> AvatarKeys.fileId(object.key()).isPresent())
        .collect(Collectors.groupingBy(object -> AvatarKeys.fileId(object.key()).orElseThrow()));

    List<UUID> ids = List.copyOf(byFileId.keySet());
    Set<UUID> existing = new HashSet<>();
    for (int from = 0; from < ids.size(); from += ID_BATCH) {
      existing.addAll(mediaFileRepository.findExistingIds(ids.subList(from, Math.min(ids.size(), from + ID_BATCH))));
    }

    int deleted = 0;
    for (Map.Entry<UUID, List<StoredObject>> entry : byFileId.entrySet()) {
      boolean orphan = !existing.contains(entry.getKey())
          && entry.getValue().stream().allMatch(object -> object.lastModified().isBefore(threshold));
      if (!orphan) {
        continue;
      }
      for (StoredObject object : entry.getValue()) {
        try {
          storageService.delete(object.key());
          deleted++;
        } catch (RuntimeException e) {
          log.warn("Не удалось удалить файл-сироту {}: {}", object.key(), e.toString());
        }
      }
    }
    return deleted;
  }

  /** @return сколько записей удалено */
  public int deleteStalePending(Instant now) {
    Integer deleted = transactionTemplate.execute(status -> {
      List<MediaFile> stale = mediaFileRepository.findByStatusAndCreatedAtBefore(
          MediaFileStatus.PENDING, now.minus(PENDING_TTL));
      for (MediaFile file : stale) {
        mediaFileRepository.delete(file);
        mediaFileCleanup.deleteAfterCommit(List.of(file.getStorageKey()));
      }
      return stale.size();
    });
    return deleted == null ? 0 : deleted;
  }
}
