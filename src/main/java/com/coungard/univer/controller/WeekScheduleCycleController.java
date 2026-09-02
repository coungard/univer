package com.coungard.univer.controller;

import com.coungard.univer.dto.WeekScheduleCycleDto;
import com.coungard.univer.dto.request.UpdateWeekScheduleCycleStatusRequest;
import com.coungard.univer.service.WeekScheduleCycleService;
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
@RequestMapping("/api/v1/week-schedule-cycles")
@RequiredArgsConstructor
@Tag(name = "WeekScheduleCycles", description = "Циклическое расписание семестра")
@SecurityRequirement(name = "bearerAuth")
public class WeekScheduleCycleController {

  private final WeekScheduleCycleService weekScheduleCycleService;

  @Operation(summary = "Получить циклы расписания с пагинацией")
  @GetMapping
  public ResponseEntity<Page<WeekScheduleCycleDto>> getWeekScheduleCycles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<WeekScheduleCycleDto> cycles = weekScheduleCycleService.getWeekScheduleCycles(pageable);
    return ResponseEntity.ok(cycles);
  }

  @Operation(summary = "Получить цикл по ID семестра")
  @GetMapping("/semester/{semesterId}")
  public ResponseEntity<WeekScheduleCycleDto> getWeekScheduleCycleBySemester(@PathVariable UUID semesterId) {
    WeekScheduleCycleDto dto = weekScheduleCycleService.getWeekScheduleCycleBySemester(semesterId);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Получить цикл по ID")
  @GetMapping("/{id}")
  public ResponseEntity<WeekScheduleCycleDto> getWeekScheduleCycleById(@PathVariable UUID id) {
    WeekScheduleCycleDto dto = weekScheduleCycleService.getWeekScheduleCycleById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать цикл расписания для семестра")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<WeekScheduleCycleDto> createWeekScheduleCycle(
      @Valid @RequestBody WeekScheduleCycleDto cycleDto) {
    WeekScheduleCycleDto saved = weekScheduleCycleService.createWeekScheduleCycle(cycleDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Удалить цикл расписания")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteWeekScheduleCycle(@PathVariable UUID id) {
    weekScheduleCycleService.deleteWeekScheduleCycle(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Изменить статус согласования цикла (DRAFT/AGREED)",
      description = "Пока цикл в DRAFT, Pair цикла может править ADMIN или STUDENT своей группы; "
          + "в AGREED — только ADMIN. Переход разрешён в обе стороны."
  )
  @PutMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<WeekScheduleCycleDto> updateStatus(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateWeekScheduleCycleStatusRequest request) {

    WeekScheduleCycleDto updated = weekScheduleCycleService.updateStatus(id, request.status());
    return ResponseEntity.ok(updated);
  }
}
