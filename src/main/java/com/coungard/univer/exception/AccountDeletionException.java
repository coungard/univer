package com.coungard.univer.exception;

/**
 * Удалить аккаунт не получилось из-за внешней системы (Keycloak недоступен) — отдаётся клиенту как 500 с
 * понятным сообщением, а не общим «Внутренняя ошибка сервера» (issue #133). Данные в БД при этом не
 * удалены: транзакция откатывается, и удаление можно просто повторить.
 */
public class AccountDeletionException extends RuntimeException {

  public AccountDeletionException(String message, Throwable cause) {
    super(message, cause);
  }
}
