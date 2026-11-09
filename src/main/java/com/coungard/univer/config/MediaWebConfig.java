package com.coungard.univer.config;

import java.nio.file.Path;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Раздача медиа-файлов самим приложением ({@code media.serve-from-app}), чтобы публичные ссылки работали при
 * локальной разработке без Caddy (issue #136). Доступ без токена — {@code permitAll} на {@code /media/**} в
 * {@link SecurityConfig}. Листинга каталога у resource handler'а нет, выход за пределы каталога он отсекает сам.
 */
@Configuration
@RequiredArgsConstructor
public class MediaWebConfig implements WebMvcConfigurer {

  public static final String MEDIA_PATH = "/media/**";

  private final MediaConfig mediaConfig;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!mediaConfig.isServeFromApp()) {
      return;
    }
    String location = Path.of(mediaConfig.getStorageDir()).toAbsolutePath().normalize().toUri().toString();
    registry.addResourceHandler(MEDIA_PATH)
        .addResourceLocations(location.endsWith("/") ? location : location + "/")
        // Каждая загрузка получает новый ключ, поэтому содержимое по ключу не меняется
        .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());
  }
}
