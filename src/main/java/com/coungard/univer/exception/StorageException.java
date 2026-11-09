package com.coungard.univer.exception;

/** Сбой хранилища медиа-файлов (диск недоступен, нет места и т.п.), а не ошибка в запросе клиента. */
public class StorageException extends RuntimeException {

  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
