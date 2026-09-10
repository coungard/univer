package com.coungard.univer.repository;

import com.coungard.univer.entity.University;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UniversityRepository extends JpaRepository<University, java.util.UUID> {

  Page<University> findByNameContainingIgnoreCase(String name, Pageable pageable);
}