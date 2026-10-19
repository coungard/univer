package com.coungard.univer.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Ответ 401 как у resource server по умолчанию ({@link BearerTokenAuthenticationEntryPoint}) плюс
 * строка WARN в логе (issue #110). Отклонённый цепочкой Spring Security запрос не доходит до
 * {@code RequestLoggingFilter}, поэтому без этого 401 в логе не видно вовсе.
 */
@Slf4j
public class LoggingAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    log.warn("401 {} {} ip={}: {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr(),
        authException.getMessage());
    delegate.commence(request, response, authException);
  }
}
