package com.coungard.univer.repository;

import com.coungard.univer.entity.Faculty;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    Page<Faculty> findByUniversityId(UUID universityId, Pageable pageable);

    long countByCreatedByStudentId(UUID createdByStudentId);

    long countByUniversityId(UUID universityId);

    /**
     * Число факультетов по каждому из университетов одним запросом. Университеты без факультетов
     * в результат не попадают.
     */
    @Query("""
        SELECT f.university.id AS universityId, COUNT(f) AS facultyCount
        FROM Faculty f
        WHERE f.university.id IN :universityIds
        GROUP BY f.university.id
        """)
    List<UniversityFacultyCount> countByUniversityIds(@Param("universityIds") Collection<UUID> universityIds);

    interface UniversityFacultyCount {
        UUID getUniversityId();

        long getFacultyCount();
    }

    /**
     * Факультеты университета с таким же названием без учёта регистра и крайних пробелов.
     */
    @Query("""
        SELECT f FROM Faculty f
        WHERE f.university.id = :universityId
          AND LOWER(TRIM(f.name)) = LOWER(TRIM(:name))
        ORDER BY f.id
        """)
    List<Faculty> findByUniversityIdAndNameIgnoreCase(
        @Param("universityId") UUID universityId, @Param("name") String name);
}
