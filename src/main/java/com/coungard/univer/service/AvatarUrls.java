package com.coungard.univer.service;

import com.coungard.univer.dto.AvatarDto;
import com.coungard.univer.entity.MediaFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Публичные ссылки на аватар для DTO (issue #137). Нужен только id файла — у ленивого прокси
 * {@code Person.avatarFile} он есть без запроса к БД.
 */
@Component
@RequiredArgsConstructor
public class AvatarUrls {

  private final StorageService storageService;

  public String full(MediaFile avatar) {
    return avatar == null ? null : storageService.publicUrl(AvatarKeys.full(avatar.getId()));
  }

  public String thumbnail(MediaFile avatar) {
    return avatar == null ? null : storageService.publicUrl(AvatarKeys.thumbnail(avatar.getId()));
  }

  public AvatarDto toDto(MediaFile avatar) {
    return AvatarDto.builder()
        .fileId(avatar.getId())
        .avatarUrl(full(avatar))
        .avatarThumbnailUrl(thumbnail(avatar))
        .build();
  }
}
