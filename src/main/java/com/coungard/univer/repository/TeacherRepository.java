package com.coungard.univer.repository;

import com.coungard.univer.entity.Teacher;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

  boolean existsByPersonEmail(String email);

  boolean existsByPersonUsername(String username);

}