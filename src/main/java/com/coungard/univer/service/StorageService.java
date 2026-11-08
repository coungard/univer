package com.coungard.univer.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;

/**
 * Хранилище медиа-файлов (issue #136). В БД хранится ключ объекта, а не URL и не путь на диске: так переезд с
 * локального диска в облачное S3 (критерии — issue #135) сводится к другой реализации этого интерфейса и копированию
 * файлов, без миграции данных.
 * <p>
 * Ключ — относительный путь из сегментов через {@code /}, например {@code avatars/{fileId}/512.jpg}. Допустимы
 * латинские буквы, цифры, {@code .}, {@code _} и {@code -}; сегмент начинается с буквы или цифры (поэтому
 * {@code ..}, скрытые файлы и абсолютные пути невозможны), длина — не больше {@value #MAX_KEY_LENGTH} символов.
 * Ключи формирует сервер, а не клиент, поэтому недопустимый ключ — ошибка программиста
 * ({@link IllegalArgumentException}).
 */
public interface StorageService {

  /** Совпадает с длиной {@code media_files.storage_key}. */
  int MAX_KEY_LENGTH = 512;

  /**
   * Сохраняет объект, заменяя существующий с тем же ключом. Запись атомарная: читатели видят либо прежний объект,
   * либо новый целиком, но не частично записанный.
   *
   * @param contentType MIME-тип; локальному диску не нужен, но понадобится облачному хранилищу
   */
  void put(String key, InputStream content, String contentType);

  default void put(String key, byte[] content, String contentType) {
    put(key, new ByteArrayInputStream(content), contentType);
  }

  /**
   * Удаляет объект.
   *
   * @return {@code false}, если объекта не было — это не ошибка
   */
  boolean delete(String key);

  boolean exists(String key);

  /**
   * Все объекты с ключами, начинающимися с {@code prefix} (например, {@code avatars/}), — для уборки сирот.
   *
   * @param prefix пустая строка или ключ-каталог с {@code /} на конце
   */
  List<StoredObject> list(String prefix);

  /** Постоянный публичный адрес объекта для клиента. Существование объекта не проверяется. */
  String publicUrl(String key);

  /** Объект хранилища и время его последней записи. */
  record StoredObject(String key, Instant lastModified) {
  }
}
