package com.coungard.univer.repository;

import com.coungard.univer.entity.Semester;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {

  Page<Semester> findByStudyYearId(UUID studyYearId, Pageable pageable);
}
