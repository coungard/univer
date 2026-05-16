package com.coungard.univer.repository;

import com.coungard.univer.entity.Program;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {

  Page<Program> findByFacultyId(UUID facultyId, Pageable pageable);
}