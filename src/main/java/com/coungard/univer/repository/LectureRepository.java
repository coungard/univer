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
   * Lecture, сгенерированные из пары, по которым посещаемость ещё не отмечалась ни преподавателем, ни
   * самим студентом (issue #117), — их можно удалить и создать заново при изменении или удалении пары.
   */
  @Query("""
      SELECT l FROM Lecture l
      WHERE l.sourcePair.id = :pairId
        AND NOT EXISTS (SELECT 1 FROM LectureAttendance a WHERE a.lectureId = l.id)
        AND NOT EXISTS (SELECT 1 FROM LectureSelfAttendance s WHERE s.lectureId = l.id)
      """)
  List<Lecture> findBySourcePairIdWithoutAttendance(@Param("pairId") UUID pairId);

  /** Лекция есть в расписании группы — напрямую или как поток. */
  boolean existsByIdAndGroupsId(UUID id, UUID groupId);

  /** Лекции, по которым у студента есть отметка — преподавателя или личная. */
  @Query("""
      SELECT l FROM Lecture l
      WHERE EXISTS (SELECT 1 FROM LectureAttendance a WHERE a.lectureId = l.id AND a.studentId = :studentId)
         OR EXISTS (SELECT 1 FROM LectureSelfAttendance s WHERE s.lectureId = l.id AND s.studentId = :studentId)
      """)
  Page<Lecture> findMarkedByStudentId(@Param("studentId") UUID studentId, Pageable pageable);

  /** ID лекций курса у группы, начавшихся в промежутке {@code [from, to]}. */
  @Query("""
      SELECT l.id FROM Lecture l JOIN l.groups g
      WHERE g.id = :groupId
        AND l.course.id = :courseId
        AND l.scheduledTime BETWEEN :from AND :to
      """)
  List<UUID> findIdsByGroupAndCourseBetween(
      @Param("groupId") UUID groupId,
      @Param("courseId") UUID courseId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  /**
   * Отвязать от пары все сгенерированные из неё Lecture — перед удалением пары, чтобы оставшиеся
   * лекции не ссылались на несуществующую запись.
   */
  @Modifying
  @Query("UPDATE Lecture l SET l.sourcePair = null WHERE l.sourcePair.id = :pairId")
  int detachFromSourcePair(@Param("pairId") UUID pairId);
}
