package com.coungard.univer.controller;

import com.coungard.univer.dto.FeedbackAdminDto;
import com.coungard.univer.dto.FeedbackDto;
import com.coungard.univer.dto.FeedbackStatus;
import com.coungard.univer.dto.FeedbackType;
import com.coungard.univer.dto.request.SubmitFeedbackRequest;
import com.coungard.univer.dto.request.UpdateFeedbackStatusRequest;
import com.coungard.univer.security.CurrentActor;
import com.coungard.univer.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Отзывы из мобильного приложения: ошибки, доработки, идеи")
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @Operation(
      summary = "Оставить отзыв",
      description = "Автор берётся из токена. Не чаще " + FeedbackService.RATE_LIMIT + " раза в "
          + FeedbackService.RATE_LIMIT_WINDOW_HOURS + " ч от одного пользователя."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Отзыв сохранён"),
      @ApiResponse(responseCode = "400", description = "Невалидное тело: карта поле → сообщение"),
      @ApiResponse(responseCode = "429", description = "Превышен лимит отзывов в час; см. заголовок Retry-After")
  })
  @PostMapping
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
  public ResponseEntity<FeedbackDto> submitFeedback(
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody SubmitFeedbackRequest request) {

    // Роль есть всегда: без одной из трёх ролей сюда не пустит @PreAuthorize
    String role = CurrentActor.role().orElseThrow();
    FeedbackDto created = feedbackService.submit(
        UUID.fromString(jwt.getSubject()), role, jwt.getClaimAsString("preferred_username"), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Operation(summary = "Получить отзывы с пагинацией", description = "Сначала новые. Фильтры необязательны.")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<FeedbackAdminDto>> getFeedback(
      @Parameter(description = "Тип отзыва; без него — все типы") @RequestParam(required = false) FeedbackType type,
      @Parameter(description = "Статус; без него — все статусы") @RequestParam(required = false) FeedbackStatus status,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    return ResponseEntity.ok(feedbackService.getFeedback(type, status, pageable));
  }

  @Operation(
      summary = "Сменить статус отзыва",
      description = "NEW → IN_PROGRESS → DONE | REJECTED; из NEW можно сразу в DONE или REJECTED. DONE и "
          + "REJECTED окончательные. Тот же статус ничего не меняет."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Статус изменён"),
      @ApiResponse(responseCode = "404", description = "Отзыв не найден"),
      @ApiResponse(responseCode = "422", description = "Недопустимый переход статуса")
  })
  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<FeedbackAdminDto> updateStatus(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateFeedbackStatusRequest request) {

    return ResponseEntity.ok(feedbackService.updateStatus(id, request.status()));
  }
}
