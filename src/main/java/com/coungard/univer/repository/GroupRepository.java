package com.coungard.univer.repository;

import com.coungard.univer.entity.Group;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

  Page<Group> findBySemesterId(UUID semesterId, Pageable pageable);

  /**
   * Находит все группы, относящиеся (через semester -> studyYear -> program -> faculty) к
   * указанному университету.
   */
  @Query("SELECT g FROM Group g WHERE g.semester.studyYear.program.facultyId IN " +
      "(SELECT f.id FROM Faculty f WHERE f.university.id = :universityId)")
  Page<Group> findByUniversityId(@Param("universityId") UUID universityId, Pageable pageable);
}
