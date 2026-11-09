package com.coungard.univer.dto;

import java.util.UUID;
import lombok.Builder;

/**
 * Аватар текущего пользователя — ответ на загрузку (issue #137). Ссылки постоянные и публичные (без токена):
 * каждая загрузка получает новый {@code fileId}, поэтому по ссылке всегда одно и то же содержимое и клиент
 * может кэшировать картинку бессрочно.
 *
 * @param fileId             id файла; удобный ключ кэша на клиенте
 * @param avatarUrl          JPEG 512×512
 * @param avatarThumbnailUrl JPEG 128×128 — для списков
 */
@Builder
public record AvatarDto(
    UUID fileId,
    String avatarUrl,
    String avatarThumbnailUrl
) {
}
