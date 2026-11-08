package com.coungard.univer.exception;

import com.coungard.univer.filter.RequestIdFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.NOT_FOUND.value());
    body.put("error", "Not Found");
    body.put("message", ex.getMessage());
    body.put("path", "/");

    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Object> handleValidation(ValidationException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
    body.put("error", "Not Found");
    body.put("message", ex.getMessage());
    body.put("path", "/");

    return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Object> handleConflict(ConflictException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.CONFLICT.value());
    body.put("error", "Conflict");
    body.put("message", ex.getMessage());
    body.put("field", ex.getField());
    if (ex.getExistingId() != null) {
      body.put("id", ex.getExistingId());
    }
    body.put("path", "/");

    return new ResponseEntity<>(body, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<Object> handleTooManyRequests(TooManyRequestsException ex) {
    // Retry-After в целых секундах с округлением вверх, не меньше секунды
    long retryAfterSeconds = Math.max(1, (ex.getRetryAfter().toMillis() + 999) / 1000);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
    body.put("error", "Too Many Requests");
    body.put("message", ex.getMessage());
    body.put("retryAfterSeconds", retryAfterSeconds);
    body.put("path", "/");

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds))
        .body(body);
  }

  /**
   * Удаление аккаунта сорвалось на Keycloak — 500 с понятным сообщением и {@code requestId}, как у
   * необработанных ошибок, причина со стеком в лог.
   */
  @ExceptionHandler(AccountDeletionException.class)
  public ResponseEntity<Object> handleAccountDeletion(AccountDeletionException ex, HttpServletRequest request) {
    log.error("Не удалось удалить аккаунт: {} {}", request.getMethod(), request.getRequestURI(), ex);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());
    body.put("requestId", MDC.get(RequestIdFilter.MDC_KEY));
    body.put("path", request.getRequestURI());

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Значение поля тела не того формата — неизвестное значение enum, не UUID, не число: такая же плоская карта
   * поле → сообщение, как у ошибок {@code @Valid}, а не стандартное тело Spring. Прочие нечитаемые тела
   * (битый JSON, нет тела) остаются стандартным ответом 400.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleNotReadable(HttpMessageNotReadableException ex) throws Exception {
    if (!(ex.getCause() instanceof InvalidFormatException invalidFormat) || invalidFormat.getPath().isEmpty()) {
      throw ex;
    }

    String field = invalidFormat.getPath().stream()
        .map(reference -> reference.getFieldName() != null
            ? reference.getFieldName()
            : "[" + reference.getIndex() + "]")
        .collect(Collectors.joining(".")).replace(".[", "[");

    Class<?> targetType = invalidFormat.getTargetType();
    String message = targetType != null && targetType.isEnum()
        ? "Недопустимое значение. Допустимые: " + String.join(", ", enumJsonValues(targetType))
        : "Недопустимое значение";
    return new ResponseEntity<>(Map.of(field, message), HttpStatus.BAD_REQUEST);
  }

  /** Значения enum, как они выглядят в JSON: с учётом {@code @JsonProperty} на константах. */
  private static List<String> enumJsonValues(Class<?> enumType) {
    return Arrays.stream(enumType.getEnumConstants())
        .map(constant -> {
          String name = ((Enum<?>) constant).name();
          try {
            JsonProperty property = enumType.getField(name).getAnnotation(JsonProperty.class);
            return property != null ? property.value() : name;
          } catch (NoSuchFieldException e) {
            return name;
          }
        })
        .toList();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Необработанные исключения — 500 с единообразным телом без деталей и стека, а сама ошибка со стеком
   * пишется в лог с {@code requestId} того же запроса (issue #110), чтобы по id из ответа её найти.
   * <p>
   * Исключения, которые и раньше превращались в осмысленный ответ без этого обработчика (400/404/405 и
   * прочие стандартные ошибки Spring MVC, {@code @ResponseStatus}, отказ в доступе), пробрасываются
   * дальше: повторно выброшенное исходное исключение Spring разрешает так, будто обработчик не совпал.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUnexpected(Exception ex, HttpServletRequest request) throws Exception {
    if (isResolvedBySpring(ex)) {
      throw ex;
    }

    log.error("Необработанная ошибка: {} {}", request.getMethod(), request.getRequestURI(), ex);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", "Внутренняя ошибка сервера");
    body.put("requestId", MDC.get(RequestIdFilter.MDC_KEY));
    body.put("path", request.getRequestURI());

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /** Те же типы, что разрешают {@code DefaultHandlerExceptionResolver} и Spring Security. */
  private boolean isResolvedBySpring(Exception ex) {
    return ex instanceof ErrorResponse
        || ex instanceof TypeMismatchException
        || ex instanceof HttpMessageNotReadableException
        || ex instanceof BindException
        || ex instanceof AccessDeniedException
        || ex instanceof AuthenticationException
        || AnnotatedElementUtils.hasAnnotation(ex.getClass(), ResponseStatus.class);
  }
}