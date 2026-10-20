package com.coungard.univer.config;

import com.coungard.univer.security.CurrentActor;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Временные метки и авторы изменений сущностей — см. {@code AuditableEntity} (issue #112).
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<UUID> auditorAware() {
    return CurrentActor::id;
  }

  /**
   * Время с точностью до микросекунд, как его хранит Postgres: иначе ответ на создание отдал бы
   * {@code createdAt} точнее, чем последующие чтения той же записи (issue #119).
   */
  @Bean
  public DateTimeProvider auditingDateTimeProvider() {
    return () -> Optional.of(Instant.now().truncatedTo(ChronoUnit.MICROS));
  }
}
