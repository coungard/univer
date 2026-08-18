package com.coungard.univer.repository;

import com.coungard.univer.entity.StudyYear;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyYearRepository extends JpaRepository<StudyYear, UUID> {

  Page<StudyYear> findByProgramId(UUID programId, Pageable pageable);

  boolean existsByProgramIdAndYearNumber(UUID programId, Integer yearNumber);
}
