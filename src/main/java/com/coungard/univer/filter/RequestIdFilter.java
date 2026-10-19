package com.coungard.univer.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Сквозной идентификатор запроса (issue #110): берётся из заголовка {@code X-Request-Id}, если клиент
 * его прислал и он похож на идентификатор, иначе генерируется. На время запроса лежит в MDC под ключом
 * {@code requestId} — его выводит {@code logging.pattern.level}, поэтому он есть в каждой строке лога
 * этого запроса, — и возвращается клиенту в заголовке ответа.
 * <p>
 * Выполняется раньше цепочки Spring Security ({@link Ordered#HIGHEST_PRECEDENCE}), чтобы id был и в
 * ответах 401/403, которые она отдаёт сама, и в её логах.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

  public static final String HEADER = "X-Request-Id";
  public static final String MDC_KEY = "requestId";

  // Чужой id попадает в логи как есть — пропускаем только безопасные символы и разумную длину
  private static final Pattern VALID_REQUEST_ID = Pattern.compile("[A-Za-z0-9._-]{1,64}");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String requestId = resolveRequestId(request.getHeader(HEADER));
    MDC.put(MDC_KEY, requestId);
    response.setHeader(HEADER, requestId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }

  private String resolveRequestId(String header) {
    if (header != null && VALID_REQUEST_ID.matcher(header).matches()) {
      return header;
    }
    return UUID.randomUUID().toString();
  }
}
