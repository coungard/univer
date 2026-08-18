package com.coungard.univer.controller;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import java.util.UUID;

@Tag(name = "Faculties", description = "CRUD и поиск факультетов с пагинацией")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/faculties")
@RequiredArgsConstructor
public class FacultyController {

  private final FacultyService facultyService;

  @PostMapping
  @Operation(summary = "Добавить факультет")
  public ResponseEntity<FacultyDto> createFaculty(@RequestBody FacultyDto facultyDto) {
    FacultyDto created = facultyService.createFaculty(facultyDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(created.id())
        .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/university/{universityId}")
  @Operation(summary = "Получить факультеты по ID университета с пагинацией")
  public ResponseEntity<Page<FacultyDto>> getFacultiesByUniversity(
      @PathVariable UUID universityId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<FacultyDto> faculties = facultyService.getFacultiesByUniversity(universityId, pageable);
    return ResponseEntity.ok(faculties);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить факультет по ID")
  public ResponseEntity<FacultyDto> getFacultyById(@PathVariable UUID id) {
    FacultyDto faculty = facultyService.getFacultyById(id);
    return ResponseEntity.ok(faculty);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Изменить факультет по ID ")
  public ResponseEntity<FacultyDto> updateFaculty(
      @PathVariable UUID id,
      @RequestBody FacultyDto facultyDto) {
    FacultyDto updated = facultyService.updateFaculty(id, facultyDto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Удалить факультет по ID")
  public ResponseEntity<Void> deleteFaculty(@PathVariable UUID id) {
    facultyService.deleteFaculty(id);
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}