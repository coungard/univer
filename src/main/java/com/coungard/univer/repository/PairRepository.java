package com.coungard.univer.repository;

import com.coungard.univer.entity.Pair;
import java.time.Instant;
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
public interface PairRepository extends JpaRepository<Pair, UUID> {

  Page<Pair> findByWeekScheduleCycleId(UUID weekScheduleCycleId, Pageable pageable);

  /**
   * Все Pair цикла без пагинации — для внутренней bulk-логики (массовая генерация Lecture на весь
   * семестр), а не для отдачи клиенту постранично. См. {@code StudentRepository.findByGroupId} —
   * тот же паттерн непагинированной выборки для bulk-операций.
   */
  List<Pair> findByWeekScheduleCycleId(UUID weekScheduleCycleId);

  /**
   * Расписание конкретной группы — производная выборка всех Pair, связанных с этой группой
   * (напрямую или как участник потока), а не отдельно хранимая сущность. См. TARGET.md, раздел
   * «Поток: одна пара — несколько групп».
   */
  Page<Pair> findByGroupsId(UUID groupId, Pageable pageable);

  /**
   * Отвязать преподавателя от всех его пар — при удалении его аккаунта (issue #133). Расписание групп
   * остаётся, пара просто становится «без преподавателя». Bulk-update минует JPA auditing, поэтому
   * {@code updatedAt}/{@code updatedBy} проставляются явно.
   */
  @Modifying
  @Query("""
      UPDATE Pair p SET p.teacher = null, p.updatedAt = :now, p.updatedBy = :actorId
      WHERE p.teacher.id = :teacherId""")
  int detachTeacher(@Param("teacherId") UUID teacherId, @Param("now") Instant now,
      @Param("actorId") UUID actorId);
}
