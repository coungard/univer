package com.coungard.univer.controller;

import com.coungard.univer.dto.TeacherDto;
import com.coungard.univer.dto.registration.RegisterTeacherRequest;
import com.coungard.univer.service.TeacherService;
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
import org.springframework.validation.annotation.Validated;
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

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "CRUD и поиск преподавателей с пагинацией")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class TeacherController {

  private final TeacherService teacherService;

  @Operation(
      summary = "Получить преподавателей с пагинацией и фильтрацией",
      description = "Поддерживает поиск по имени, университету"
  )
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<TeacherDto>> getTeachers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<TeacherDto> teachers = teacherService.getTeachers(pageable);
    return ResponseEntity.ok(teachers);
  }

  @Operation(summary = "Получить преподавателя по ID")
  @GetMapping("/{id}")
  public ResponseEntity<TeacherDto> getTeacherById(@PathVariable UUID id) {
    TeacherDto dto = teacherService.getTeacherById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Регистрация нового преподавателя")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Преподаватель успешно зарегистрирован"),
      @ApiResponse(responseCode = "400", description = "Некорректные данные"),
      @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
  })
  @PostMapping("/register")
  public ResponseEntity<TeacherDto> registerTeacher(@Valid @RequestBody RegisterTeacherRequest registerDto) {
    TeacherDto teacherDto = teacherService.registerTeacher(registerDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentServletMapping()
        .path("/api/v1/teachers/{id}")
        .buildAndExpand(teacherDto.id())
        .toUri();

    return ResponseEntity.created(location).body(teacherDto);
  }

  @Operation(summary = "Создать нового преподавателя")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
    TeacherDto saved = teacherService.createTeacher(teacherDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить преподавателя")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<TeacherDto> updateTeacher(
      @PathVariable UUID id,
      @Valid @RequestBody TeacherDto teacherDto) {

    TeacherDto updated = teacherService.updateTeacher(id, teacherDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить преподавателя")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteTeacher(@PathVariable UUID id) {
    teacherService.deleteTeacherById(id);
    return ResponseEntity.noContent().build();
  }
}