package com.coungard.univer.repository;

import com.coungard.univer.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Page<Department> findByFacultyId(UUID facultyId, Pageable pageable);

    boolean existsByNameAndFacultyId(String name, UUID facultyId);

    /**
     * Находит все кафедры, относящиеся к факультетам указанного университета
     */
    @Query("SELECT d FROM Department d WHERE d.faculty.id IN " +
            "(SELECT f.id FROM Faculty f WHERE f.university.id = :universityId)")
    Page<Department> findByUniversityId(@Param("universityId") UUID universityId, Pageable pageable);
}