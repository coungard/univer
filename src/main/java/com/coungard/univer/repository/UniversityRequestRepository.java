package com.coungard.univer.repository;

import com.coungard.univer.dto.UniversityRequestStatus;
import com.coungard.univer.entity.UniversityRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRequestRepository extends JpaRepository<UniversityRequest, UUID> {

  Optional<UniversityRequest> findByStudentIdAndStatus(UUID studentId, UniversityRequestStatus status);

  Optional<UniversityRequest> findFirstByStudentIdOrderByCreatedAtDesc(UUID studentId);

  Page<UniversityRequest> findByStatus(UniversityRequestStatus status, Pageable pageable);

  /**
   * Удалить все заявки студента на добавление вуза — при удалении его аккаунта (issue #133).
   */
  @Modifying
  @Query("DELETE FROM UniversityRequest r WHERE r.student.id = :studentId")
  int deleteAllOfStudent(@Param("studentId") UUID studentId);
}
