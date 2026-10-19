package com.coungard.univer.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Ответ 403 как у resource server по умолчанию ({@link BearerTokenAccessDeniedHandler}) плюс строка
 * WARN в логе с пользователем (issue #110). Сюда же приходят отказы {@code @PreAuthorize}:
 * {@code GlobalExceptionHandler} пробрасывает {@link AccessDeniedException} дальше.
 */
@Slf4j
public class LoggingAccessDeniedHandler implements AccessDeniedHandler {

  private final AccessDeniedHandler delegate = new BearerTokenAccessDeniedHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.warn("403 {} {} user={} ip={}", request.getMethod(), request.getRequestURI(),
        authentication != null ? authentication.getName() : null, request.getRemoteAddr());
    delegate.handle(request, response, accessDeniedException);
  }
}
