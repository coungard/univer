package com.coungard.univer.controller;

import com.coungard.univer.dto.PairDto;
import com.coungard.univer.service.PairService;
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
@RequestMapping("/api/v1/pairs")
@RequiredArgsConstructor
@Tag(name = "Pairs", description = "CRUD и поиск пар циклического расписания с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class PairController {

  private final PairService pairService;

  @Operation(summary = "Получить пары с пагинацией")
  @GetMapping
  public ResponseEntity<Page<PairDto>> getPairs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<PairDto> pairs = pairService.getPairs(pageable);
    return ResponseEntity.ok(pairs);
  }

  @Operation(summary = "Получить пары по ID циклического расписания с пагинацией")
  @GetMapping("/week-schedule-cycle/{weekScheduleCycleId}")
  public ResponseEntity<Page<PairDto>> getPairsByWeekScheduleCycle(
      @PathVariable UUID weekScheduleCycleId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<PairDto> pairs = pairService.getPairsByWeekScheduleCycle(weekScheduleCycleId, pageable);
    return ResponseEntity.ok(pairs);
  }

  @Operation(summary = "Получить расписание группы с пагинацией")
  @GetMapping("/group/{groupId}")
  public ResponseEntity<Page<PairDto>> getPairsByGroup(
      @PathVariable UUID groupId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<PairDto> pairs = pairService.getPairsByGroup(groupId, pageable);
    return ResponseEntity.ok(pairs);
  }

  @Operation(summary = "Получить пару по ID")
  @GetMapping("/{id}")
  public ResponseEntity<PairDto> getPairById(@PathVariable UUID id) {
    PairDto dto = pairService.getPairById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать пару")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PairDto> createPair(@Valid @RequestBody PairDto pairDto) {
    PairDto saved = pairService.createPair(pairDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить пару")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PairDto> updatePair(
      @PathVariable UUID id,
      @Valid @RequestBody PairDto pairDto) {

    PairDto updated = pairService.updatePair(id, pairDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить пару")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deletePair(@PathVariable UUID id) {
    pairService.deletePair(id);
    return ResponseEntity.noContent().build();
  }
}
