package com.coungard.univer.repository;

import com.coungard.univer.entity.Pair;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
