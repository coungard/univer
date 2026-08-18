package com.coungard.univer.controller;

import com.coungard.univer.dto.SemesterDto;
import com.coungard.univer.service.SemesterService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/semesters")
@RequiredArgsConstructor
@Tag(name = "Semesters", description = "CRUD и поиск семестров с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class SemesterController {

  private final SemesterService semesterService;

  @Operation(summary = "Получить семестры с пагинацией")
  @GetMapping
  public ResponseEntity<Page<SemesterDto>> getSemesters(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<SemesterDto> semesters = semesterService.getSemesters(pageable);
    return ResponseEntity.ok(semesters);
  }

  @Operation(summary = "Получить семестры по ID курса обучения с пагинацией")
  @GetMapping("/study-year/{studyYearId}")
  public ResponseEntity<Page<SemesterDto>> getSemestersByStudyYear(
      @PathVariable UUID studyYearId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<SemesterDto> semesters = semesterService.getSemestersByStudyYear(studyYearId, pageable);
    return ResponseEntity.ok(semesters);
  }

  @Operation(summary = "Получить семестр по ID")
  @GetMapping("/{id}")
  public ResponseEntity<SemesterDto> getSemesterById(@PathVariable UUID id) {
    SemesterDto dto = semesterService.getSemesterById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать семестр")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SemesterDto> createSemester(@Valid @RequestBody SemesterDto semesterDto) {
    SemesterDto saved = semesterService.createSemester(semesterDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить семестр")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SemesterDto> updateSemester(
      @PathVariable UUID id,
      @Valid @RequestBody SemesterDto semesterDto) {

    SemesterDto updated = semesterService.updateSemester(id, semesterDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить семестр")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteSemester(@PathVariable UUID id) {
    semesterService.deleteSemester(id);
    return ResponseEntity.noContent().build();
  }
}
