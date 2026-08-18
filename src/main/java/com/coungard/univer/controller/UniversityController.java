package com.coungard.univer.controller;

import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
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

@RestController
@RequestMapping("/api/v1/universities")
@RequiredArgsConstructor
@Tag(name = "Universities", description = "CRUD и поиск университетов с пагинацией")
@SecurityRequirement(name = "bearerAuth") // Подключаем Bearer Token
public class UniversityController {

  private final UniversityService universityService;

  @GetMapping
  @Operation(summary = "Получить университеты с пагинацией")
  public ResponseEntity<Page<UniversityDto>> getUniversities(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<UniversityDto> universities = universityService.getUniversities(pageable);
    return ResponseEntity.ok(universities);
  }

  @Operation(summary = "Получить университет по ID")
  @ApiResponse(responseCode = "200", description = "Университет найден")
  @ApiResponse(responseCode = "404", description = "Университет не найден",
      content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping("/{id}")
  public ResponseEntity<UniversityDto> getUniversityById(
      @Parameter(description = "ID университета", example = "a8f2e7b1-1c2d-4e3f-8a9b-1c2d3e4f5a6b")
      @PathVariable UUID id) {
    UniversityDto dto = universityService.getUniversityById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать университет")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UniversityDto> createUniversity(@Valid @RequestBody UniversityDto universityDto) {
    UniversityDto saved = universityService.createUniversity(universityDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UniversityDto> updateUniversity(
      @PathVariable UUID id,
      @Valid @RequestBody UniversityDto universityDto) {

    UniversityDto updated = universityService.updateUniversity(id, universityDto);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUniversity(@PathVariable UUID id) {
    universityService.deleteUniversityById(id);
    return ResponseEntity.noContent().build();
  }
}