package com.coungard.univer.repository;

import com.coungard.univer.entity.WeekScheduleCycle;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekScheduleCycleRepository extends JpaRepository<WeekScheduleCycle, UUID> {

  Optional<WeekScheduleCycle> findBySemesterId(UUID semesterId);

  boolean existsBySemesterId(UUID semesterId);

  /**
   * Завести семестру цикл в статусе {@code DRAFT}, если цикла у него ещё нет. {@code ON CONFLICT}
   * вместо «проверить и вставить»: при двух одновременных запросах второй дожидается первого и ничего
   * не вставляет, а не падает на уникальности {@code semester_id}.
   *
   * @return 1, если цикл создан; 0, если он уже был
   */
  @Modifying
  @Query(value = """
      INSERT INTO week_schedule_cycles (id, semester_id, status)
      VALUES (gen_random_uuid(), :semesterId, 'DRAFT')
      ON CONFLICT (semester_id) DO NOTHING
      """, nativeQuery = true)
  int insertDraftIfAbsent(@Param("semesterId") UUID semesterId);
}
