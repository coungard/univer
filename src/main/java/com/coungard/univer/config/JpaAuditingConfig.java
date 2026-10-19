package com.coungard.univer.config;

import com.coungard.univer.security.CurrentActor;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Временные метки и авторы изменений сущностей — см. {@code AuditableEntity} (issue #112).
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

  @Bean
  public AuditorAware<UUID> auditorAware() {
    return CurrentActor::id;
  }
}
