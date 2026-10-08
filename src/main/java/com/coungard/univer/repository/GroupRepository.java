package com.coungard.univer.repository;

import com.coungard.univer.entity.Group;
import java.time.LocalDate;
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
   * Группы «актуального» на дату {@code today} семестра, с необязательными фильтрами по факультету
   * (группа → семестр → учебный год → факультет) и номеру курса.
   *
   * <p>Актуальный семестр выбирается отдельно для каждого учебного года ({@code StudyYear}), в три
   * ступени: идущий сейчас ({@code startDate <= today <= endDate}); если такого нет — ближайший
   * будущий; если нет и будущих — последний закончившийся.
   *
   * <p>Незаданный фильтр записан как {@code COALESCE(:param, поле)}, а не {@code :param IS NULL OR ...}:
   * для параметра, который встречается только в {@code IS NULL}, PostgreSQL не может вывести тип
   * («could not determine data type of parameter»).
   */
  @Query("""
      SELECT g FROM Group g
        JOIN g.semester s
        JOIN s.studyYear sy
      WHERE sy.facultyId = COALESCE(:facultyId, sy.facultyId)
        AND sy.yearNumber = COALESCE(:yearNumber, sy.yearNumber)
        AND (
          (s.startDate <= :today AND s.endDate >= :today)
          OR (
            s.startDate > :today
            AND NOT EXISTS (SELECT 1 FROM Semester cur WHERE cur.studyYear = sy
                AND cur.startDate <= :today AND cur.endDate >= :today)
            AND NOT EXISTS (SELECT 1 FROM Semester earlier WHERE earlier.studyYear = sy
                AND earlier.startDate > :today AND earlier.startDate < s.startDate)
          )
          OR (
            s.endDate < :today
            AND NOT EXISTS (SELECT 1 FROM Semester later WHERE later.studyYear = sy
                AND later.endDate > s.endDate)
          )
        )
      ORDER BY g.name, g.id
      """)
  Page<Group> findCurrentSemesterGroups(
      @Param("facultyId") UUID facultyId,
      @Param("yearNumber") Integer yearNumber,
      @Param("today") LocalDate today,
      Pageable pageable);
}
