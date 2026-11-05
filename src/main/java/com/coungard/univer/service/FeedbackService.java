package com.coungard.univer.service;

import com.coungard.univer.dto.FeedbackAdminDto;
import com.coungard.univer.dto.FeedbackDto;
import com.coungard.univer.dto.FeedbackStatus;
import com.coungard.univer.dto.FeedbackType;
import com.coungard.univer.dto.request.SubmitFeedbackRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Отзывы из мобильного приложения (issue #130).
 */
public interface FeedbackService {

  /**
   * Сколько отзывов пользователь может оставить за {@link #RATE_LIMIT_WINDOW_HOURS} ч.
   */
  int RATE_LIMIT = 1;

  int RATE_LIMIT_WINDOW_HOURS = 1;

  /**
   * Оставить отзыв.
   *
   * @param userId   автор — subject JWT
   * @param role     старшая роль автора без префикса {@code ROLE_}
   * @param username {@code preferred_username} автора, если он есть в токене
   * @throws com.coungard.univer.exception.TooManyRequestsException автор уже оставил {@link #RATE_LIMIT}
   *                                                                отзывов за последний час
   */
  FeedbackDto submit(UUID userId, String role, String username, SubmitFeedbackRequest request);

  /**
   * Получить страницу отзывов для администратора. Фильтры необязательны и объединяются через «и».
   */
  Page<FeedbackAdminDto> getFeedback(FeedbackType type, FeedbackStatus status, Pageable pageable);

  /**
   * Сменить статус отзыва. Тот же статус ничего не меняет.
   *
   * @throws com.coungard.univer.exception.ResourceNotFoundException отзыв не найден
   * @throws com.coungard.univer.exception.ValidationException        переход недопустим, см.
   *                                                                  {@link FeedbackStatus#canMoveTo}
   */
  FeedbackAdminDto updateStatus(UUID id, FeedbackStatus status);
}
