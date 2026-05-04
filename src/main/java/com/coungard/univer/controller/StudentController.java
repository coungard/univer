package com.coungard.univer.controller;

import com.coungard.univer.dto.RegisterStudentDto;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "CRUD и поиск студентов с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

  private final StudentService studentService;

  @Operation(
      summary = "Получить студентов с пагинацией и фильтрацией",
      description = "Поддерживает поиск по имени, университету и дате зачисления"
  )
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<StudentDto>> getStudents(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) UUID universityId,
      @RequestParam(required = false) LocalDate enrollmentDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "lastName") String sort,
      @RequestParam(defaultValue = "asc") String direction) {

    Page<StudentDto> students = studentService.getStudents(name, universityId, enrollmentDate, page, size, sort,
        direction);
    return ResponseEntity.ok(students);
  }

  @Operation(summary = "Получить студента по ID")
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<StudentDto> getStudentById(@PathVariable UUID id) {
    StudentDto dto = studentService.getStudentById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Регистрация нового студента")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Студент успешно зарегистрирован"),
      @ApiResponse(responseCode = "400", description = "Некорректные данные"),
      @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
  })
  @PostMapping("/register")
  public ResponseEntity<StudentDto> registerStudent(@Valid @RequestBody RegisterStudentDto registerDto) {
    StudentDto studentDto = studentService.registerStudent(registerDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentServletMapping()
        .path("/api/v1/students/{id}")
        .buildAndExpand(studentDto.id())
        .toUri();

    return ResponseEntity.created(location).body(studentDto);
  }

  @Operation(summary = "Создать нового студента")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
    StudentDto saved = studentService.createStudent(studentDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить студента")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StudentDto> updateStudent(
      @PathVariable UUID id,
      @Valid @RequestBody StudentDto studentDto) {

    StudentDto updated = studentService.updateStudent(id, studentDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить студента")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
    studentService.deleteStudentById(id);
    return ResponseEntity.noContent().build();
  }
}