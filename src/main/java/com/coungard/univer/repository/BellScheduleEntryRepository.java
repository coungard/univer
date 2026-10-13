package com.coungard.univer.repository;

import com.coungard.univer.entity.BellScheduleEntry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BellScheduleEntryRepository extends JpaRepository<BellScheduleEntry, UUID> {

  Optional<BellScheduleEntry> findByUniversityIdAndPairNumber(UUID universityId, Integer pairNumber);

  Optional<BellScheduleEntry> findByUniversityIsNullAndPairNumber(Integer pairNumber);

  Page<BellScheduleEntry> findByUniversityId(UUID universityId, Pageable pageable);

  /** Все собственные записи университета без пагинации — их единицы, по одной на номер пары. */
  List<BellScheduleEntry> findByUniversityId(UUID universityId);

  /** Все записи по умолчанию ({@code university == null}). */
  List<BellScheduleEntry> findByUniversityIsNull();

  boolean existsByUniversityIdAndPairNumber(UUID universityId, Integer pairNumber);

  boolean existsByUniversityIsNullAndPairNumber(Integer pairNumber);
}
