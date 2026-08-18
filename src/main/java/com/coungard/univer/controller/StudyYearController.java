package com.coungard.univer.controller;

import com.coungard.univer.dto.StudyYearDto;
import com.coungard.univer.service.StudyYearService;
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
@RequestMapping("/api/v1/study-years")
@RequiredArgsConstructor
@Tag(name = "StudyYears", description = "CRUD и поиск курсов обучения с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class StudyYearController {

  private final StudyYearService studyYearService;

  @Operation(summary = "Получить курсы обучения с пагинацией")
  @GetMapping
  public ResponseEntity<Page<StudyYearDto>> getStudyYears(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<StudyYearDto> studyYears = studyYearService.getStudyYears(pageable);
    return ResponseEntity.ok(studyYears);
  }

  @Operation(summary = "Получить курсы обучения по ID программы с пагинацией")
  @GetMapping("/program/{programId}")
  public ResponseEntity<Page<StudyYearDto>> getStudyYearsByProgram(
      @PathVariable UUID programId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<StudyYearDto> studyYears = studyYearService.getStudyYearsByProgram(programId, pageable);
    return ResponseEntity.ok(studyYears);
  }

  @Operation(summary = "Получить курс обучения по ID")
  @GetMapping("/{id}")
  public ResponseEntity<StudyYearDto> getStudyYearById(@PathVariable UUID id) {
    StudyYearDto dto = studyYearService.getStudyYearById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать курс обучения")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StudyYearDto> createStudyYear(@Valid @RequestBody StudyYearDto studyYearDto) {
    StudyYearDto saved = studyYearService.createStudyYear(studyYearDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить курс обучения")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<StudyYearDto> updateStudyYear(
      @PathVariable UUID id,
      @Valid @RequestBody StudyYearDto studyYearDto) {

    StudyYearDto updated = studyYearService.updateStudyYear(id, studyYearDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить курс обучения")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteStudyYear(@PathVariable UUID id) {
    studyYearService.deleteStudyYear(id);
    return ResponseEntity.noContent().build();
  }
}
