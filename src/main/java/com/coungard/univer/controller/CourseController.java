package com.coungard.univer.controller;

import com.coungard.univer.dto.CourseDto;
import com.coungard.univer.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "CRUD и поиск курсов с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

  private final CourseService courseService;

  @Operation(
      summary = "Получить курсы с пагинацией",
      description = "С параметром facultyId — только предметы этого факультета, отсортированные по "
          + "названию: привязанные к факультету напрямую (в том числе созданные студентами) и "
          + "привязанные через его кафедры. Без параметра — все курсы."
  )
  @GetMapping
  public ResponseEntity<Page<CourseDto>> getCourses(
      @RequestParam(required = false) UUID facultyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<CourseDto> courses = facultyId != null
        ? courseService.getCoursesByFaculty(facultyId, pageable)
        : courseService.getCourses(pageable);
    return ResponseEntity.ok(courses);
  }

  @Operation(summary = "Получить курсы по ID кафедры с пагинацией")
  @GetMapping("/department/{departmentId}")
  public ResponseEntity<Page<CourseDto>> getCoursesByDepartment(
      @PathVariable UUID departmentId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<CourseDto> courses = courseService.getCoursesByDepartment(departmentId, pageable);
    return ResponseEntity.ok(courses);
  }

  @Operation(summary = "Получить курс по ID")
  @GetMapping("/{id}")
  public ResponseEntity<CourseDto> getCourseById(@PathVariable UUID id) {
    CourseDto dto = courseService.getCourseById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(
      summary = "Создать курс",
      description = "Если указан facultyId, название должно быть свободно среди предметов этого "
          + "факультета. Студент создаёт предмет через POST /students/me/course."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Курс создан"),
      @ApiResponse(responseCode = "409", description = "Предмет с таким названием на этом факультете уже "
          + "есть; в теле — field: \"title\" и id существующего предмета"),
      @ApiResponse(responseCode = "422", description = "Кафедра не относится к указанному факультету")
  })
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
    CourseDto saved = courseService.createCourse(courseDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить курс")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Курс обновлён"),
      @ApiResponse(responseCode = "409", description = "Другой предмет с таким названием на этом факультете "
          + "уже есть; в теле — field: \"title\" и id существующего предмета"),
      @ApiResponse(responseCode = "422", description = "Кафедра не относится к указанному факультету")
  })
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<CourseDto> updateCourse(
      @PathVariable UUID id,
      @Valid @RequestBody CourseDto courseDto) {

    CourseDto updated = courseService.updateCourse(id, courseDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить курс")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }
}
