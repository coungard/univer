package com.coungard.univer.repository;

import com.coungard.univer.entity.Lecture;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, UUID> {

  Page<Lecture> findByCourseId(UUID courseId, Pageable pageable);

  /**
   * Расписание конкретной группы — производная выборка всех Lecture, связанных с этой группой
   * (напрямую или как участник потока).
   */
  Page<Lecture> findByGroupsId(UUID groupId, Pageable pageable);

  boolean existsBySourcePairIdAndScheduledTime(UUID sourcePairId, LocalDateTime scheduledTime);
}
