package com.coungard.univer.service;

import com.coungard.univer.dto.RegisterStudentDto;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.mapper.StudentMapper;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.validation.StudentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;
    private final StudentMapper studentMapper;
    private final StudentValidator studentValidator;

    private final KeycloakAdminService keycloakAdminService;

    @Transactional(readOnly = true)
    public Page<StudentDto> getStudents(
            String name,
            UUID universityId,
            LocalDate enrollmentDate,
            int page,
            int size,
            String sort,
            String direction) {

        // Формируем условия фильтрации
        Specification<Student> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            String likePattern = "%" + name.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("firstname")), likePattern),
                            cb.like(cb.lower(root.get("lastname")), likePattern)
                    )
            );
        }

        if (universityId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("university").get("id"), universityId));
        }

        if (enrollmentDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("enrollmentDate"), enrollmentDate));
        }

        // Настройка сортировки
        Sort sortable = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortable);

        return studentRepository.findAll(spec, pageable).map(studentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentDto registerStudent(RegisterStudentDto registerDto) {

        String keycloakUserId = null;
        studentValidator.validateRegisterData(registerDto);
        try {
            University university = universityRepository.findById(registerDto.universityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University not found"));


            // 2. Создаём пользователя в Keycloak
            keycloakUserId = keycloakAdminService.createUser(registerDto);

            // 3. Назначаем роль STUDENT
            keycloakAdminService.assignStudentRole(keycloakUserId);

            Student student = new Student();
            student.setId(UUID.fromString(keycloakUserId)); // Используем Keycloak ID как ID студента
            student.setUsername(registerDto.username().toLowerCase());
            student.setFirstname(registerDto.firstname());
            student.setLastname(registerDto.lastname());
            student.setFullname(registerDto.fullname());
            student.setEmail(registerDto.email());
            student.setEnrollmentDate(registerDto.enrollmentDate());
            student.setUniversity(university);

            Student saved = studentRepository.save(student);
            return studentMapper.toDto(saved);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            // Откат: если Keycloak-пользователь был создан, но БД упала
            if (keycloakUserId != null) {
                try {
                    keycloakAdminService.deleteUser(keycloakUserId);
                    log.info("Пользователь в Keycloak удалён после сбоя в БД: " + keycloakUserId);
                } catch (Exception cleanupEx) {
                    log.warn("Не удалось удалить пользователя в Keycloak: " + keycloakUserId);
                    log.error(cleanupEx.getMessage(), cleanupEx);
                }
            }
            throw new RuntimeException("Ошибка при регистрации студента: " + ex.getMessage());
        }
    }

    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        validateUniversityExists(studentDto.universityId());
        Student student = studentMapper.toEntity(studentDto);
        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
    }

    @Transactional
    public StudentDto updateStudent(UUID id, StudentDto studentDto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        validateUniversityExists(studentDto.universityId());

        existing.setUsername(studentDto.username());
        existing.setFirstname(studentDto.firstname());
        existing.setLastname(studentDto.lastname());
        existing.setFullname(studentDto.fullname());
        existing.setEmail(studentDto.email());
        existing.setEnrollmentDate(studentDto.enrollmentDate());

        University university = universityRepository.findById(studentDto.universityId())
                .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + studentDto.universityId()));
        existing.setUniversity(university);

        Student updated = studentRepository.save(existing);
        return studentMapper.toDto(updated);
    }

    @Transactional
    public void deleteStudentById(UUID id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // Вспомогательный метод
    private void validateUniversityExists(UUID universityId) {
        if (!universityRepository.existsById(universityId)) {
            throw new ResourceNotFoundException("University not found with id: " + universityId);
        }
    }
}