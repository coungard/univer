package com.coungard.univer.controller;

import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import com.coungard.univer.dto.request.CreateStudentGroupRequest;
import com.coungard.univer.dto.request.UpdateStudentProfileRequest;
import com.coungard.univer.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "CRUD и поиск студентов с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

  private final StudentService studentService;

  @Operation(summary = "Получить студентов с пагинацией")
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<StudentDto>> getStudents(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<StudentDto> students = studentService.getStudents(pageable);
    return ResponseEntity.ok(students);
  }

  @Operation(
      summary = "Получить свой профиль",
      description = "ID студента берётся из JWT (Keycloak subject = Student.id, см. флоу регистрации)."
  )
  @GetMapping("/me")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<StudentDto> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
    StudentDto dto = studentService.getStudentById(UUID.fromString(jwt.getSubject()));
    return ResponseEntity.ok(dto);
  }

  @Operation(
      summary = "Заполнить свой профиль: университет, факультет, курс, группа",
      description = "Частичное обновление: переданное поле меняется, непереданное — нет, явный null "
          + "очищает поле. Смена поля сбрасывает всё, что ниже по цепочке университет → факультет → "
          + "курс → группа; незаполненные поля выше по цепочке проставляются по выбранному значению."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Профиль обновлён"),
      @ApiResponse(responseCode = "404", description = "Университет, факультет или группа не найдены"),
      @ApiResponse(responseCode = "422", description = "Поля не согласованы между собой")
  })
  @PatchMapping("/me")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<StudentDto> updateMyProfile(
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody UpdateStudentProfileRequest request) {

    StudentDto updated = studentService.updateMyProfile(UUID.fromString(jwt.getSubject()), request);
    return ResponseEntity.ok(updated);
  }

  @Operation(
      summary = "Создать свою группу и зачислиться в неё",
      description = "Для случая, когда нужной группы нет в GET /groups?facultyId=&yearNumber=. Факультет "
          + "и курс берутся из профиля студента, семестр подбирается на сервере: актуальный семестр "
          + "курса, а если учебного года или семестров ещё нет — они создаются. Созданная группа сразу "
          + "видна в списке групп курса."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Группа создана, студент зачислен в неё"),
      @ApiResponse(responseCode = "409", description = "Группа с таким названием на этом курсе уже есть; "
          + "в теле — field: \"name\" и id существующей группы"),
      @ApiResponse(responseCode = "422", description = "В профиле не выбраны факультет и курс")
  })
  @PostMapping("/me/group")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<StudentDto> createMyGroup(
      @AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CreateStudentGroupRequest request) {

    StudentDto updated = studentService.createMyGroup(UUID.fromString(jwt.getSubject()), request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentServletMapping()
        .path("/api/v1/groups/{id}")
        .buildAndExpand(updated.groupId())
        .toUri();

    return ResponseEntity.created(location).body(updated);
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
  public ResponseEntity<StudentDto> registerStudent(@Valid @RequestBody RegisterStudentRequest registerDto) {
    StudentDto studentDto = studentService.registerStudent(registerDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentServletMapping()
        .path("/api/v1/students/{id}")
        .buildAndExpand(studentDto.id())
        .toUri();

    return ResponseEntity.created(location).body(studentDto);
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