package com.coungard.univer.repository;

import com.coungard.univer.entity.Course;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    Page<Course> findByDepartmentId(UUID departmentId, Pageable pageable);

    long countByCreatedByStudentId(UUID createdByStudentId);

    /**
     * Предметы факультета: привязанные к нему напрямую (в том числе созданные студентами) и
     * привязанные через кафедру этого факультета. Отсортированы по названию.
     */
    @Query("""
        SELECT c FROM Course c
          LEFT JOIN c.department d
        WHERE (c.faculty.id = :facultyId OR d.faculty.id = :facultyId)
        ORDER BY c.title, c.id
        """)
    Page<Course> findByFacultyId(@Param("facultyId") UUID facultyId, Pageable pageable);

    /**
     * Предметы факультета (см. {@link #findByFacultyId}) с таким же названием без учёта регистра и
     * крайних пробелов.
     */
    @Query("""
        SELECT c FROM Course c
          LEFT JOIN c.department d
        WHERE (c.faculty.id = :facultyId OR d.faculty.id = :facultyId)
          AND LOWER(TRIM(c.title)) = LOWER(TRIM(:title))
        ORDER BY c.id
        """)
    List<Course> findByFacultyIdAndTitleIgnoreCase(
        @Param("facultyId") UUID facultyId, @Param("title") String title);

    /**
     * Отвязать преподавателя от всех его предметов — при удалении его аккаунта (issue #133). Bulk-update
     * минует JPA auditing, поэтому {@code updatedAt}/{@code updatedBy} проставляются явно.
     */
    @Modifying
    @Query("""
        UPDATE Course c SET c.teacher = null, c.updatedAt = :now, c.updatedBy = :actorId
        WHERE c.teacher.id = :teacherId""")
    int detachTeacher(@Param("teacherId") UUID teacherId, @Param("now") Instant now,
        @Param("actorId") UUID actorId);
}
