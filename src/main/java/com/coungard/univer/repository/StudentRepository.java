package com.coungard.univer.repository;

import com.coungard.univer.entity.Student;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

  boolean existsByPersonEmail(String email);

  boolean existsByPersonUsername(String username);

  List<Student> findByGroupId(UUID groupId);
}