package com.coungard.univer.service.impl;

import com.coungard.univer.dto.AuditAction;
import com.coungard.univer.dto.AuditEntityType;
import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.entity.Course;
import com.coungard.univer.entity.Department;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.Teacher;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.CourseMapper;
import com.coungard.univer.repository.CourseRepository;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.TeacherRepository;
import com.coungard.univer.service.AuditService;
import com.coungard.univer.service.CourseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;
  private final DepartmentRepository departmentRepository;
  private final FacultyRepository facultyRepository;
  private final TeacherRepository teacherRepository;
  private final CourseMapper courseMapper;
  private final AuditService auditService;

  @Override
  @Transactional
  public CourseDto createCourse(CourseDto courseDto) {
    Course course = courseMapper.toEntity(courseDto);
    course.setDepartment(resolveDepartment(courseDto.departmentId()));
    course.setFaculty(resolveFaculty(courseDto.facultyId(), course.getDepartment()));
    course.setTeacher(resolveTeacher(courseDto.teacherId()));
    if (course.getFaculty() != null) {
      course.setTitle(course.getTitle().strip());
      validateTitleIsFree(course.getFaculty().getId(), course.getTitle(), null);
    }

    Course saved = courseRepository.save(course);
    return courseMapper.toDto(saved);
  }

  @Override
  @Transactional
  public CourseDto createCourseByStudent(UUID facultyId, String title, UUID createdByStudentId) {
    Faculty faculty = resolveFaculty(facultyId, null);

    String newTitle = title.strip();
    validateTitleIsFree(facultyId, newTitle, null);

    Course course = new Course();
    course.setTitle(newTitle);
    course.setFaculty(faculty);
    course.setCreatedByStudentId(createdByStudentId);

    Course saved = courseRepository.save(course);
    return courseMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public CourseDto getCourseById(UUID id) {
    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс не найден с ID: " + id));
    return courseMapper.toDto(course);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CourseDto> getCourses(Pageable pageable) {
    return courseRepository.findAll(pageable).map(courseMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CourseDto> getCoursesByDepartment(UUID departmentId, Pageable pageable) {
    return courseRepository.findByDepartmentId(departmentId, pageable).map(courseMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CourseDto> getCoursesByFaculty(UUID facultyId, Pageable pageable) {
    return courseRepository.findByFacultyId(facultyId, pageable).map(courseMapper::toDto);
  }

  @Override
  @Transactional
  public CourseDto updateCourse(UUID id, CourseDto courseDto) {
    Course existing = courseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Курс не найден с ID: " + id));

    existing.setTitle(courseDto.title());
    existing.setDescription(courseDto.description());
    existing.setDepartment(resolveDepartment(courseDto.departmentId()));
    existing.setFaculty(resolveFaculty(courseDto.facultyId(), existing.getDepartment()));
    existing.setTeacher(resolveTeacher(courseDto.teacherId()));
    if (existing.getFaculty() != null) {
      existing.setTitle(existing.getTitle().strip());
      validateTitleIsFree(existing.getFaculty().getId(), existing.getTitle(), id);
    }

    Course updated = courseRepository.saveAndFlush(existing);
    return courseMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteCourse(UUID id) {
    if (!courseRepository.existsById(id)) {
      throw new ResourceNotFoundException("Курс не найден с ID: " + id);
    }
    courseRepository.deleteById(id);
    auditService.record(AuditAction.DELETE, AuditEntityType.COURSE, id);
  }

  private Department resolveDepartment(UUID departmentId) {
    if (departmentId == null) {
      return null;
    }
    return departmentRepository.findById(departmentId)
        .orElseThrow(() -> new ResourceNotFoundException("Кафедра не найдена с ID: " + departmentId));
  }

  /**
   * Факультет прямой привязки предмета. Если у предмета есть и кафедра, она должна относиться к
   * этому же факультету — иначе предмет попадал бы в списки двух факультетов.
   */
  private Faculty resolveFaculty(UUID facultyId, Department department) {
    if (facultyId == null) {
      return null;
    }
    Faculty faculty = facultyRepository.findById(facultyId)
        .orElseThrow(() -> new ResourceNotFoundException("Факультет не найден с ID: " + facultyId));
    if (department != null && !department.getFaculty().getId().equals(facultyId)) {
      throw new ValidationException("Кафедра не относится к указанному факультету");
    }
    return faculty;
  }

  /**
   * Название предмета уникально в пределах факультета без учёта регистра и крайних пробелов —
   * среди всех предметов факультета, включая привязанные через кафедру.
   *
   * @param selfId ID самого обновляемого предмета — он себе не мешает; {@code null} при создании
   */
  private void validateTitleIsFree(UUID facultyId, String title, UUID selfId) {
    courseRepository.findByFacultyIdAndTitleIgnoreCase(facultyId, title).stream()
        .filter(course -> !course.getId().equals(selfId))
        .findFirst()
        .ifPresent(course -> {
          throw new ConflictException("title",
              "Предмет с таким названием на этом факультете уже есть: " + course.getTitle(), course.getId());
        });
  }

  private Teacher resolveTeacher(UUID teacherId) {
    if (teacherId == null) {
      return null;
    }
    return teacherRepository.findById(teacherId)
        .orElseThrow(() -> new ResourceNotFoundException("Преподаватель не найден с ID: " + teacherId));
  }
}
