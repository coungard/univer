package com.coungard.univer.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Логирует каждый входящий REST-запрос: метод, путь, статус ответа, длительность обработки и
 * аутентифицированного пользователя (subject из JWT, если запрос его нёс).
 * <p>
 * Регистрируется автоматически как servlet-фильтр благодаря {@link Component} (стандартное
 * поведение Spring Boot для бинов типа {@code Filter}), порядок явно не задаётся — фильтр
 * выполняется после цепочки Spring Security (её {@code FilterChainProxy} зарегистрирован с более
 * высоким приоритетом), поэтому на момент логирования {@link SecurityContextHolder} уже содержит
 * результат аутентификации.
 * <p>
 * Намеренно НЕ логирует тело запроса/ответа и заголовки (в частности {@code Authorization}) —
 * там могут быть пароли (см. флоу регистрации, {@code RegisterStudentRequest}/
 * {@code RegisterTeacherRequest}) и сами JWT. См. issue #69.
 * <p>
 * Запросы, отклонённые самой цепочкой Spring Security (401/403), сюда не доходят — их логируют
 * {@code LoggingAuthenticationEntryPoint}/{@code LoggingAccessDeniedHandler}. Идентификатор запроса
 * в каждой строке — из MDC, его кладёт {@link RequestIdFilter} (issue #110).
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final String ANONYMOUS_PRINCIPAL = "anonymous";
  private static final List<String> SWAGGER_PATH_PREFIXES = List.of("/swagger-ui", "/v3/api-docs", "/webjars");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    long startedAtMs = System.currentTimeMillis();
    try {
      filterChain.doFilter(request, response);
    } finally {
      long durationMs = System.currentTimeMillis() - startedAtMs;
      String format = "{} {} -> {} ({} ms) user={} ip={}";
      Object[] args = {
          request.getMethod(),
          requestUriWithQuery(request),
          response.getStatus(),
          durationMs,
          resolvePrincipal(),
          request.getRemoteAddr()};
      // Swagger UI тянет десятки статических ресурсов на одно открытие — на INFO это шум (issue #110)
      if (isSwaggerRequest(request)) {
        log.debug(format, args);
      } else {
        log.info(format, args);
      }
    }
  }

  private boolean isSwaggerRequest(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return SWAGGER_PATH_PREFIXES.stream().anyMatch(uri::startsWith);
  }

  private String requestUriWithQuery(HttpServletRequest request) {
    String queryString = request.getQueryString();
    return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
  }

  private String resolvePrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ANONYMOUS_PRINCIPAL;
    }
    return authentication.getName();
  }
}
