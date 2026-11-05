package com.coungard.univer.dto.request;

import com.coungard.univer.dto.FeedbackPlatform;
import com.coungard.univer.dto.FeedbackType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Тело {@code POST /feedback}: отзыв из мобильного приложения. Автора клиент не передаёт — он берётся из
 * токена.
 *
 * <p>Крайние пробелы строк отбрасываются ещё до валидации, поэтому ограничения длины относятся к тексту без
 * них, а сообщение из одних пробелов считается пустым.
 */
public record SubmitFeedbackRequest(

    @NotNull(message = "Тип отзыва обязателен")
    FeedbackType type,

    @NotBlank(message = "Текст отзыва обязателен")
    @Size(max = 2000, message = "Текст отзыва не длиннее 2000 символов")
    String message,

    @NotBlank(message = "Версия приложения обязательна")
    @Size(max = 32, message = "Версия приложения не длиннее 32 символов")
    String appVersion,

    @NotNull(message = "Платформа обязательна")
    FeedbackPlatform platform,

    @NotBlank(message = "Версия ОС обязательна")
    @Size(max = 32, message = "Версия ОС не длиннее 32 символов")
    String osVersion
) {

  public SubmitFeedbackRequest {
    message = strip(message);
    appVersion = strip(appVersion);
    osVersion = strip(osVersion);
  }

  private static String strip(String value) {
    return value == null ? null : value.strip();
  }
}
