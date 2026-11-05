package com.coungard.univer.dto;

import java.util.Set;

/**
 * Статус разбора отзыва ({@code Feedback}) администратором: {@code NEW → IN_PROGRESS → DONE | REJECTED}.
 * Взять в работу можно и сразу закрыть: из {@code NEW} доступны все три следующих статуса. {@code DONE} и
 * {@code REJECTED} — окончательные.
 */
public enum FeedbackStatus {

  /** Только что оставлен, администратор его ещё не смотрел. */
  NEW,

  /** Администратор разбирает отзыв. */
  IN_PROGRESS,

  /** Ошибка исправлена или предложение сделано. */
  DONE,

  /** Не будет сделано: дубль, не ошибка, не в планах. */
  REJECTED;

  /**
   * Можно ли перевести отзыв из этого статуса в {@code next}.
   */
  public boolean canMoveTo(FeedbackStatus next) {
    return switch (this) {
      case NEW -> Set.of(IN_PROGRESS, DONE, REJECTED).contains(next);
      case IN_PROGRESS -> Set.of(DONE, REJECTED).contains(next);
      case DONE, REJECTED -> false;
    };
  }
}
