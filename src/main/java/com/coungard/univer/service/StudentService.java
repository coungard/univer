package com.coungard.univer.service;

import com.coungard.univer.dto.RegisterStudentDto;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.mapper.StudentMapper;
import com.coungard.univer.entity.Student;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.repository.StudentRepository;
import com.coungard.univer.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;
    private final StudentMapper studentMapper;

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
                            cb.like(cb.lower(root.get("firstName")), likePattern),
                            cb.like(cb.lower(root.get("lastName")), likePattern)
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

        return studentRepository.findAll(spec, pageable)
                .map(studentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentDto registerStudent(RegisterStudentDto registerDto) {
        // 1. Проверяем, что университет существует
        University university = universityRepository.findById(registerDto.universityId())
                .orElseThrow(() -> new ResourceNotFoundException("University not found"));

        // 2. Создаём пользователя в Keycloak
        String keycloakUserId = keycloakAdminService.createUser(
                registerDto.firstName(),
                registerDto.lastName(),
                registerDto.email(),
                registerDto.password()
        );

        // 3. Назначаем роль STUDENT
        keycloakAdminService.assignStudentRole(keycloakUserId);

        // 4. Создаём сущность Student
        Student student = new Student();
        student.setId(UUID.fromString(keycloakUserId)); // Используем Keycloak ID как ID студента
        student.setFirstName(registerDto.firstName());
        student.setLastName(registerDto.lastName());
        student.setEmail(registerDto.email());
        student.setEnrollmentDate(registerDto.enrollmentDate());
        student.setUniversity(university);

        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
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

        existing.setFirstName(studentDto.firstName());
        existing.setLastName(studentDto.lastName());
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