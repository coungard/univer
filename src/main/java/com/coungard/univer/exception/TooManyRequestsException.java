package com.coungard.univer.exception;

import java.time.Duration;
import lombok.Getter;

/**
 * Превышен лимит частоты действия (например, отзывов в час) — отдаётся клиенту как 429 с заголовком
 * {@code Retry-After}.
 */
@Getter
public class TooManyRequestsException extends RuntimeException {

  /**
   * Через сколько действие снова станет доступно.
   */
  private final Duration retryAfter;

  public TooManyRequestsException(String message, Duration retryAfter) {
    super(message);
    this.retryAfter = retryAfter;
  }
}
