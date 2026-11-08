package com.coungard.univer.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Ключи файлов аватара в {@link StorageService} (issue #137): {@code avatars/{fileId}/512.jpg} и
 * {@code avatars/{fileId}/128.jpg}. Новый {@code fileId} на каждую загрузку — поэтому содержимое по ключу не
 * меняется и кэшируется бессрочно.
 */
public final class AvatarKeys {

  public static final String PREFIX = "avatars/";

  private AvatarKeys() {
  }

  public static String full(UUID fileId) {
    return PREFIX + fileId + "/" + AvatarImageProcessor.FULL_SIZE + ".jpg";
  }

  public static String thumbnail(UUID fileId) {
    return PREFIX + fileId + "/" + AvatarImageProcessor.THUMBNAIL_SIZE + ".jpg";
  }

  public static List<String> all(UUID fileId) {
    return List.of(full(fileId), thumbnail(fileId));
  }

  /** {@code fileId} из ключа {@code avatars/{fileId}/...}; пусто — ключ не аватара или не того вида. */
  public static Optional<UUID> fileId(String key) {
    if (!key.startsWith(PREFIX)) {
      return Optional.empty();
    }
    int end = key.indexOf('/', PREFIX.length());
    if (end < 0) {
      return Optional.empty();
    }
    try {
      return Optional.of(UUID.fromString(key.substring(PREFIX.length(), end)));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
