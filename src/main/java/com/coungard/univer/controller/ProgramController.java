package com.coungard.univer.controller;

import com.coungard.univer.dto.ProgramDto;
import com.coungard.univer.dto.request.CreateProgramRequest;
import com.coungard.univer.service.ProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
@Tag(name = "Programs", description = "Контроллер для работы с образовательными программами")
@SecurityRequirement(name = "bearerAuth")
public class ProgramController {

  private final ProgramService programService;

  @PostMapping
//  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Создать новую образовательную программу")
  public ResponseEntity<ProgramDto> createProgram(@Valid @RequestBody CreateProgramRequest request) {
    ProgramDto dto = programService.createProgram(request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(dto.id())
        .toUri();

    return ResponseEntity.created(location).body(dto);
  }

  @GetMapping("/{id}")
//  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
  @Operation(summary = "Получить программу по ID")
  public ResponseEntity<ProgramDto> getProgramById(@PathVariable UUID id) {
    ProgramDto dto = programService.getProgramById(id);
    return ResponseEntity.ok(dto);
  }

  @GetMapping
//  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
  @Operation(summary = "Поиск образовательных программ")
  public ResponseEntity<Page<ProgramDto>> searchPrograms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ProgramDto> programs = programService.getPrograms(pageable);
    return ResponseEntity.ok(programs);
  }

  @GetMapping("/faculty/{facultyId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
  @Operation(summary = "Получить программы по факультету")
  public ResponseEntity<Page<ProgramDto>> getProgramsByFaculty(
      @PathVariable UUID facultyId,
      @Parameter(hidden = true) Pageable pageable) {
    Page<ProgramDto> programs = programService.getProgramsByFaculty(facultyId, pageable);
    return ResponseEntity.ok(programs);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Обновить программу")
  public ResponseEntity<ProgramDto> updateProgram(
      @PathVariable UUID id,
      @Valid @RequestBody CreateProgramRequest request) {
    ProgramDto dto = programService.updateProgram(id, request);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Удалить программу")
  public ResponseEntity<Void> deleteProgram(@PathVariable UUID id) {
    programService.deleteProgram(id);
    return ResponseEntity.noContent().build();
  }
}