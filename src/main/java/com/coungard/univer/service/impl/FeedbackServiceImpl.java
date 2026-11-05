package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.FeedbackAdminDto;
import com.coungard.univer.dto.FeedbackDto;
import com.coungard.univer.dto.FeedbackStatus;
import com.coungard.univer.dto.FeedbackType;
import com.coungard.univer.dto.request.SubmitFeedbackRequest;
import com.coungard.univer.entity.Feedback;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.TooManyRequestsException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.FeedbackMapper;
import com.coungard.univer.repository.FeedbackRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.FeedbackService;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

  private static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(RATE_LIMIT_WINDOW_HOURS);

  private final FeedbackRepository feedbackRepository;
  private final FeedbackMapper feedbackMapper;
  private final AuditService auditService;

  @Override
  @Transactional
  public FeedbackDto submit(UUID userId, String role, String username, SubmitFeedbackRequest request) {
    checkRateLimit(userId);

    Feedback feedback = new Feedback();
    feedback.setUserId(userId);
    feedback.setRole(role);
    feedback.setUsername(username);
    feedback.setType(request.type());
    feedback.setMessage(request.message());
    feedback.setAppVersion(request.appVersion());
    feedback.setPlatform(request.platform());
    feedback.setOsVersion(request.osVersion());

    return feedbackMapper.toDto(feedbackRepository.save(feedback));
  }

  /**
   * Проверка «подсчитать, потом вставить» без блокировки: два одновременных запроса на границе лимита
   * могут оба пройти. Для формы отзыва, которую отправляют руками, это допустимо — лимит защищает от
   * зацикленного клиента, а не считает точно.
   */
  private void checkRateLimit(UUID userId) {
    Instant now = Instant.now();
    Instant windowStart = now.minus(RATE_LIMIT_WINDOW);
    if (feedbackRepository.countByUserIdAndCreatedAtAfter(userId, windowStart) < RATE_LIMIT) {
      return;
    }
    // Место освободится, когда самый старый отзыв в окне выйдет из него
    Duration retryAfter = feedbackRepository.findFirstByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(userId, windowStart)
        .map(oldest -> Duration.between(now, oldest.getCreatedAt().plus(RATE_LIMIT_WINDOW)))
        .filter(duration -> !duration.isNegative())
        .orElse(Duration.ZERO);
    throw new TooManyRequestsException(
        "Отзывы можно оставлять не чаще " + RATE_LIMIT + " раза в " + RATE_LIMIT_WINDOW_HOURS + " ч. Попробуйте позже",
        retryAfter);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<FeedbackAdminDto> getFeedback(FeedbackType type, FeedbackStatus status, Pageable pageable) {
    Specification<Feedback> filter = Specification.where(null);
    if (type != null) {
      filter = filter.and((root, query, cb) -> cb.equal(root.get("type"), type));
    }
    if (status != null) {
      filter = filter.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    return feedbackRepository.findAll(filter, pageable).map(feedbackMapper::toAdminDto);
  }

  @Override
  @Transactional
  public FeedbackAdminDto updateStatus(UUID id, FeedbackStatus status) {
    Feedback feedback = feedbackRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Отзыв не найден с ID: " + id));

    FeedbackStatus previous = feedback.getStatus();
    if (previous == status) {
      return feedbackMapper.toAdminDto(feedback);
    }
    if (!previous.canMoveTo(status)) {
      throw new ValidationException("Нельзя перевести отзыв из статуса " + previous + " в " + status);
    }

    feedback.setStatus(status);
    // saveAndFlush: updatedAt/updatedBy проставляются только на flush, а они уходят в ответ
    Feedback saved = feedbackRepository.saveAndFlush(feedback);
    auditService.record(AuditAction.STATUS_CHANGE, AuditEntityType.FEEDBACK, id, AuditService.details(
        "from", previous,
        "to", status));
    return feedbackMapper.toAdminDto(saved);
  }
}
