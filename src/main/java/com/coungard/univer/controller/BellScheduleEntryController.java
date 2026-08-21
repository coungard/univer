package com.coungard.univer.controller;

import com.coungard.univer.dto.BellScheduleEntryDto;
import com.coungard.univer.service.BellScheduleEntryService;
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
@RequestMapping("/api/v1/bell-schedule-entries")
@RequiredArgsConstructor
@Tag(name = "Bell Schedule Entries", description = "Справочник звонкового расписания (номер пары -> время) по университетам")
@SecurityRequirement(name = "bearerAuth")
public class BellScheduleEntryController {

  private final BellScheduleEntryService bellScheduleEntryService;

  @Operation(summary = "Получить записи звонкового расписания с пагинацией")
  @GetMapping
  public ResponseEntity<Page<BellScheduleEntryDto>> getEntries(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<BellScheduleEntryDto> entries = bellScheduleEntryService.getEntries(pageable);
    return ResponseEntity.ok(entries);
  }

  @Operation(summary = "Получить записи звонкового расписания конкретного университета с пагинацией")
  @GetMapping("/university/{universityId}")
  public ResponseEntity<Page<BellScheduleEntryDto>> getEntriesByUniversity(
      @PathVariable UUID universityId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<BellScheduleEntryDto> entries = bellScheduleEntryService.getEntriesByUniversity(universityId, pageable);
    return ResponseEntity.ok(entries);
  }

  @Operation(summary = "Получить запись звонкового расписания по ID")
  @GetMapping("/{id}")
  public ResponseEntity<BellScheduleEntryDto> getEntryById(@PathVariable UUID id) {
    BellScheduleEntryDto dto = bellScheduleEntryService.getEntryById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать запись звонкового расписания")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BellScheduleEntryDto> createEntry(@Valid @RequestBody BellScheduleEntryDto entryDto) {
    BellScheduleEntryDto saved = bellScheduleEntryService.createEntry(entryDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить запись звонкового расписания")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BellScheduleEntryDto> updateEntry(
      @PathVariable UUID id,
      @Valid @RequestBody BellScheduleEntryDto entryDto) {

    BellScheduleEntryDto updated = bellScheduleEntryService.updateEntry(id, entryDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить запись звонкового расписания")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteEntry(@PathVariable UUID id) {
    bellScheduleEntryService.deleteEntry(id);
    return ResponseEntity.noContent().build();
  }
}
