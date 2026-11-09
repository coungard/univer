package com.coungard.univer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Загруженный файл не принят (issue #137): {@code 413} — слишком большой (по байтам или пикселям), {@code 415} —
 * не изображение поддерживаемого формата.
 */
@Getter
public class MediaRejectedException extends RuntimeException {

  private final HttpStatus status;

  private MediaRejectedException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public static MediaRejectedException tooLarge(String message) {
    return new MediaRejectedException(HttpStatus.PAYLOAD_TOO_LARGE, message);
  }

  public static MediaRejectedException unsupported(String message) {
    return new MediaRejectedException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
  }
}
