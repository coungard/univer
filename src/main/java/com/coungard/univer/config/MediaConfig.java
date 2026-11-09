package com.coungard.univer.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Настройки хранилища медиа-файлов (issue #136), все переопределяются через env (MEDIA_*). */
@Data
@Component
public class MediaConfig {

  /** Каталог с файлами. В Docker — путь внутри volume. */
  @Value("${media.storage-dir}")
  private String storageDir;

  /** Базовый адрес, по которому клиент получает файлы: {@code <public-base-url>/<ключ>}. */
  @Value("${media.public-base-url}")
  private String publicBaseUrl;

  /**
   * Отдавать файлы самим приложением по {@code /media/**}. Нужно для локальной разработки; на стенде файлы
   * отдаёт Caddy прямо из volume, и запросы к {@code /media/**} до приложения не доходят.
   */
  @Value("${media.serve-from-app}")
  private boolean serveFromApp;
}
