package com.coungard.univer.security;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Автор текущего действия — для журнала {@code audit_events} (issue #111) и полей
 * {@code createdBy}/{@code updatedBy} (issue #112).
 */
public final class CurrentActor {

  /**
   * Роли по старшинству: автору приписывается первая из тех, что у него есть.
   */
  private static final List<Role> ROLES_BY_PRIORITY = List.of(Role.ROLE_ADMIN, Role.ROLE_TEACHER, Role.ROLE_STUDENT);

  private CurrentActor() {
  }

  /**
   * Subject JWT автора (= id студента/преподавателя) или пусто, если запрос анонимный (регистрация)
   * либо выполняется вне запроса.
   */
  public static Optional<UUID> id() {
    return authentication().flatMap(authentication -> {
      try {
        return Optional.of(UUID.fromString(authentication.getName()));
      } catch (IllegalArgumentException ex) {
        // Subject Keycloak всегда UUID; иное имя автора не учитывается
        return Optional.empty();
      }
    });
  }

  /**
   * Старшая роль автора без префикса {@code ROLE_}: {@code ADMIN}, {@code TEACHER} или {@code STUDENT}.
   */
  public static Optional<String> role() {
    return authentication().flatMap(authentication -> {
      Set<String> authorities = authentication.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.toSet());
      return ROLES_BY_PRIORITY.stream()
          .filter(role -> authorities.contains(role.name()))
          .map(role -> role.name().substring("ROLE_".length()))
          .findFirst();
    });
  }

  private static Optional<Authentication> authentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    return Optional.of(authentication);
  }
}
