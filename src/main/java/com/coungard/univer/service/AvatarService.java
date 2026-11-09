package com.coungard.univer.service;

import com.coungard.univer.dto.AvatarDto;
import com.coungard.univer.entity.MediaFile;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

/**
 * Фото профиля студента или преподавателя (issue #137).
 */
public interface AvatarService {

  /**
   * Загрузить или заменить свой аватар. Фото обрабатывается до транзакции (CPU не держит соединение с БД);
   * новые файлы пишутся в хранилище и удаляются, если транзакция откатится; прежние — удаляются после коммита.
   *
   * @param userId subject JWT = id студента или преподавателя
   * @throws com.coungard.univer.exception.MediaRejectedException  413/415 — файл не принят
   * @throws com.coungard.univer.exception.ResourceNotFoundException у пользователя нет профиля
   */
  AvatarDto upload(UUID userId, MultipartFile file);

  /** Удалить свой аватар; если его нет — ничего не делает. */
  void delete(UUID userId);

  /**
   * Удалить аватар человека, чей профиль удаляется в текущей транзакции (удаление аккаунта, issue #133).
   * Вызывать <b>после</b> удаления профиля: строка {@code persons} ссылается на файл.
   *
   * @return был ли аватар
   */
  boolean deleteOfRemovedPerson(MediaFile avatar);
}
