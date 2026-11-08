package com.coungard.univer.repository;

import com.coungard.univer.entity.Feedback;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID>, JpaSpecificationExecutor<Feedback> {

  long countByUserIdAndCreatedAtAfter(UUID userId, Instant after);

  Optional<Feedback> findFirstByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(UUID userId, Instant after);

  /**
   * Обезличить отзывы пользователя при удалении его аккаунта (issue #133): текст, тип, статус и устройство
   * остаются для разбора, а id автора, его логин и {@code created_by} стираются. Нативный запрос, потому что
   * {@code created_by} в маппинге {@code updatable = false}.
   */
  @Modifying
  @Query(value = """
      UPDATE feedback SET user_id = NULL, username = NULL, created_by = NULL
      WHERE user_id = :userId""", nativeQuery = true)
  int anonymizeAuthor(@Param("userId") UUID userId);
}
