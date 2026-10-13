package com.coungard.univer.repository;

import com.coungard.univer.entity.Lecture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, UUID> {

  Page<Lecture> findByCourseId(UUID courseId, Pageable pageable);

  /**
   * Расписание конкретной группы — производная выборка всех Lecture, связанных с этой группой
   * (напрямую или как участник потока).
   */
  Page<Lecture> findByGroupsId(UUID groupId, Pageable pageable);

  boolean existsBySourcePairId(UUID sourcePairId);

  /**
   * Время всех Lecture, сгенерированных из пары, — чтобы не создавать вторую лекцию той же пары на
   * ту же дату.
   */
  @Query("SELECT l.scheduledTime FROM Lecture l WHERE l.sourcePair.id = :pairId")
  List<LocalDateTime> findScheduledTimesBySourcePairId(@Param("pairId") UUID pairId);

  /**
   * Lecture, сгенерированные из пары, по которым посещаемость ещё не отмечалась, — их можно удалить
   * и создать заново при изменении или удалении пары.
   */
  @Query("""
      SELECT l FROM Lecture l
      WHERE l.sourcePair.id = :pairId
        AND NOT EXISTS (SELECT 1 FROM LectureAttendance a WHERE a.lectureId = l.id)
      """)
  List<Lecture> findBySourcePairIdWithoutAttendance(@Param("pairId") UUID pairId);

  /**
   * Отвязать от пары все сгенерированные из неё Lecture — перед удалением пары, чтобы оставшиеся
   * лекции не ссылались на несуществующую запись.
   */
  @Modifying
  @Query("UPDATE Lecture l SET l.sourcePair = null WHERE l.sourcePair.id = :pairId")
  int detachFromSourcePair(@Param("pairId") UUID pairId);
}
