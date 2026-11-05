package com.coungard.univer.repository;

import com.coungard.univer.entity.Feedback;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID>, JpaSpecificationExecutor<Feedback> {

  long countByUserIdAndCreatedAtAfter(UUID userId, Instant after);

  Optional<Feedback> findFirstByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(UUID userId, Instant after);
}
