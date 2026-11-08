package com.coungard.univer.dto;

/**
 * Состояние медиа-файла ({@code MediaFile}), issue #137.
 */
public enum MediaFileStatus {

  /**
   * Запись создана, файл ещё не загружен. Для будущих загрузок клиентом напрямую в хранилище; незавершённые
   * старше суток удаляет {@code MediaSweeper}.
   */
  PENDING,

  /** Файл записан в хранилище и готов к отдаче. */
  READY
}
