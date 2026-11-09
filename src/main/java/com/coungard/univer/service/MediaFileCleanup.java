package com.coungard.univer.service;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Согласует файлы в {@link StorageService} с транзакцией БД, в которой хранилище не участвует (issue #137):
 * файлы, на которые перестала ссылаться БД, удаляются только после коммита, а записанные в откатившейся
 * транзакции — после отката. Сбой удаления не ломает запрос: файл остаётся сиротой, его уберёт
 * {@link MediaSweeper}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MediaFileCleanup {

  private final StorageService storageService;

  /** Удалить файлы после коммита текущей транзакции; вне транзакции — сразу. */
  public void deleteAfterCommit(Collection<String> keys) {
    List<String> copy = List.copyOf(keys);
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      deleteQuietly(copy);
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        deleteQuietly(copy);
      }
    });
  }

  /**
   * Удалить файлы, если текущая транзакция откатится. Регистрировать до записи файлов: тогда и сбой на
   * середине записи не оставит сирот.
   */
  public void deleteOnRollback(Collection<String> keys) {
    List<String> copy = List.copyOf(keys);
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      throw new IllegalStateException("Нет активной транзакции");
    }
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if (status != STATUS_COMMITTED) {
          deleteQuietly(copy);
        }
      }
    });
  }

  private void deleteQuietly(List<String> keys) {
    for (String key : keys) {
      try {
        storageService.delete(key);
      } catch (RuntimeException e) {
        log.warn("Не удалось удалить медиа-файл {}, его уберёт MediaSweeper: {}", key, e.toString());
      }
    }
  }
}
