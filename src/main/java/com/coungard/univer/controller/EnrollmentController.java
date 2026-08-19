package com.coungard.univer.controller;

import com.coungard.univer.dto.EnrollmentDto;
import com.coungard.univer.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Зачисление студентов на курсы")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

  private final EnrollmentService enrollmentService;

  @Operation(summary = "Получить зачисление по ID студента и ID курса")
  @GetMapping("/{studentId}/{courseId}")
  public ResponseEntity<EnrollmentDto> getEnrollment(
      @PathVariable UUID studentId,
      @PathVariable UUID courseId) {

    return ResponseEntity.ok(enrollmentService.getEnrollment(studentId, courseId));
  }

  @Operation(summary = "Получить зачисления студента с пагинацией")
  @GetMapping("/student/{studentId}")
  public ResponseEntity<Page<EnrollmentDto>> getEnrollmentsByStudent(
      @PathVariable UUID studentId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId, pageable));
  }

  @Operation(summary = "Получить зачисления на курс с пагинацией")
  @GetMapping("/course/{courseId}")
  public ResponseEntity<Page<EnrollmentDto>> getEnrollmentsByCourse(
      @PathVariable UUID courseId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId, pageable));
  }

  @Operation(summary = "Зачислить студента на курс")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<EnrollmentDto> enroll(@Valid @RequestBody EnrollmentDto enrollmentDto) {
    EnrollmentDto saved = enrollmentService.enroll(enrollmentDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .replacePath("/api/v1/enrollments/{studentId}/{courseId}")
        .buildAndExpand(saved.studentId(), saved.courseId())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(
      summary = "Зачислить на курс всю группу",
      description = "Создаёт зачисление для каждого студента группы, ещё не зачисленного на этот "
          + "курс; уже зачисленные пропускаются без ошибки"
  )
  @PostMapping("/group/{groupId}/course/{courseId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<List<EnrollmentDto>> enrollGroup(
      @PathVariable UUID groupId,
      @PathVariable UUID courseId) {

    return ResponseEntity.ok(enrollmentService.enrollGroup(groupId, courseId));
  }

  @Operation(summary = "Изменить статус зачисления (COMPLETED/DROPPED/ACTIVE)")
  @PutMapping("/{studentId}/{courseId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<EnrollmentDto> updateStatus(
      @PathVariable UUID studentId,
      @PathVariable UUID courseId,
      @RequestBody EnrollmentDto enrollmentDto) {

    return ResponseEntity.ok(enrollmentService.updateStatus(studentId, courseId, enrollmentDto));
  }

  @Operation(summary = "Отчислить студента с курса")
  @DeleteMapping("/{studentId}/{courseId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<Void> unenroll(
      @PathVariable UUID studentId,
      @PathVariable UUID courseId) {

    enrollmentService.unenroll(studentId, courseId);
    return ResponseEntity.noContent().build();
  }
}
