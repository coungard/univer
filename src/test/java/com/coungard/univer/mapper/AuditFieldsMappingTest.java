package com.coungard.univer.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.coungard.univer.entity.Address;
import com.coungard.univer.entity.AuditableEntity;
import com.coungard.univer.entity.BellScheduleEntry;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Enrollment;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import com.coungard.univer.entity.LectureAttendance;
import com.coungard.univer.entity.Pair;
import com.coungard.univer.entity.Person;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.StudyYear;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.entity.University;
import com.coungard.univer.entity.UniversityRequest;
import com.coungard.univer.entity.WeekScheduleCycle;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Поля {@link AuditableEntity} доходят до DTO во всех ручных мапперах (issue #119).
 */
class AuditFieldsMappingTest {

  private static final Instant CREATED_AT = Instant.parse("2026-09-01T09:00:00Z");
  private static final Instant UPDATED_AT = Instant.parse("2026-10-01T12:30:00Z");
  private static final UUID CREATED_BY = UUID.randomUUID();
  private static final UUID UPDATED_BY = UUID.randomUUID();

  @Test
  void shouldMapAuditFieldsOfScheduleEntities() {
    StudyYear studyYear = audited(new StudyYear());
    Semester semester = audited(new Semester());
    semester.setStudyYear(studyYear);
    WeekScheduleCycle cycle = audited(new WeekScheduleCycle());
    cycle.setSemester(semester);
    Group group = audited(new Group());
    group.setSemester(semester);
    Course course = audited(new Course());

    Pair pair = audited(new Pair());
    pair.setWeekScheduleCycle(cycle);
    pair.setCourse(course);
    Lecture lecture = audited(new Lecture());
    lecture.setCourse(course);

    assertAudit(new StudyYearMapper().toDto(studyYear));
    assertAudit(new SemesterMapper().toDto(semester));
    assertAudit(new WeekScheduleCycleMapper().toDto(cycle));
    assertAudit(new GroupMapper().toDto(group));
    assertAudit(new CourseMapper().toDto(course));
    assertAudit(new PairMapper().toDto(pair));
    assertAudit(new LectureMapper().toDto(lecture));
    assertAudit(new BellScheduleEntryMapper().toDto(audited(new BellScheduleEntry())));
  }

  @Test
  void shouldMapAuditFieldsOfPeopleAndTheirRecords() {
    Student student = audited(new Student());
    student.setPerson(new Person());
    Teacher teacher = audited(new Teacher());
    teacher.setPerson(new Person());
    Course course = new Course();
    Lecture lecture = new Lecture();
    lecture.setCourse(course);

    Enrollment enrollment = audited(new Enrollment());
    enrollment.setStudent(student);
    enrollment.setCourse(course);
    LectureAttendance attendance = audited(new LectureAttendance());
    attendance.setStudent(student);
    attendance.setLecture(lecture);
    UniversityRequest request = audited(new UniversityRequest());
    request.setStudent(student);

    assertAudit(new StudentMapper().toDto(student));
    assertAudit(new TeacherMapper().toDto(teacher));
    assertAudit(new EnrollmentMapper().toDto(enrollment));
    assertAudit(new LectureAttendanceMapper().toDto(attendance));
    assertAudit(new UniversityRequestMapper().toDto(request));
  }

  @Test
  void shouldTakeLastEditorOfStudentFromPersonWhenOnlyNameChanged() {
    // Given: после создания студента правили только Person (имя), сам студент не менялся
    Student student = audited(new Student());
    Person person = audited(new Person());
    UUID nameEditor = UUID.randomUUID();
    person.setUpdatedAt(UPDATED_AT.plusSeconds(60));
    person.setUpdatedBy(nameEditor);
    student.setPerson(person);

    // When
    var dto = new StudentMapper().toDto(student);

    // Then: последнее изменение — у Person, автор и время создания — самого студента
    assertThat(dto.updatedAt()).isEqualTo(UPDATED_AT.plusSeconds(60));
    assertThat(dto.updatedBy()).isEqualTo(nameEditor);
    assertThat(dto.createdAt()).isEqualTo(CREATED_AT);
    assertThat(dto.createdBy()).isEqualTo(CREATED_BY);
  }

  @Test
  void shouldMapAuditFieldsOfUniversityAndNestedObjects() {
    Department department = audited(new Department());
    Faculty faculty = audited(new Faculty());
    faculty.setDepartments(List.of(department));
    University university = audited(new University());
    university.setAddress(audited(new Address()));
    university.setFaculties(List.of(faculty));

    var dto = new UniversityMapper().toDto(university, 1);

    assertAudit(dto);
    assertAudit(dto.address());
    assertAudit(dto.faculties().get(0));
    assertAudit(dto.faculties().get(0).departments().get(0));
  }

  private static <T extends AuditableEntity> T audited(T entity) {
    entity.setCreatedAt(CREATED_AT);
    entity.setUpdatedAt(UPDATED_AT);
    entity.setCreatedBy(CREATED_BY);
    entity.setUpdatedBy(UPDATED_BY);
    return entity;
  }

  /** DTO — record'ы без общего интерфейса, поэтому поля сравниваются по именам компонентов. */
  private static void assertAudit(Record dto) {
    assertThat(dto).as(dto.getClass().getSimpleName())
        .hasFieldOrPropertyWithValue("createdAt", CREATED_AT)
        .hasFieldOrPropertyWithValue("updatedAt", UPDATED_AT)
        .hasFieldOrPropertyWithValue("createdBy", CREATED_BY)
        .hasFieldOrPropertyWithValue("updatedBy", UPDATED_BY);
  }
}
