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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

  @Operation(
      summary = "Создать пару",
      description = "ADMIN — без ограничений. STUDENT — только для своей группы и только пока цикл "
          + "расписания в статусе DRAFT (см. WeekScheduleCycles)."
  )
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
  public ResponseEntity<PairDto> createPair(
      @Valid @RequestBody PairDto pairDto,
      @AuthenticationPrincipal Jwt jwt,
      Authentication authentication) {

    PairDto saved = pairService.createPair(pairDto, callerStudentId(jwt, authentication));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(
      summary = "Обновить пару",
      description = "ADMIN — без ограничений. STUDENT — только для своей группы и только пока цикл "
          + "расписания в статусе DRAFT (проверяется и для текущего, и для нового цикла/группы)."
  )
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
  public ResponseEntity<PairDto> updatePair(
      @PathVariable UUID id,
      @Valid @RequestBody PairDto pairDto,
      @AuthenticationPrincipal Jwt jwt,
      Authentication authentication) {

    PairDto updated = pairService.updatePair(id, pairDto, callerStudentId(jwt, authentication));
    return ResponseEntity.ok(updated);
  }

  @Operation(
      summary = "Удалить пару",
      description = "ADMIN — без ограничений. STUDENT — только для своей группы и только пока цикл "
          + "расписания в статусе DRAFT."
  )
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
  public ResponseEntity<Void> deletePair(
      @PathVariable UUID id,
      @AuthenticationPrincipal Jwt jwt,
      Authentication authentication) {

    pairService.deletePair(id, callerStudentId(jwt, authentication));
    return ResponseEntity.noContent().build();
  }

  /**
   * {@code null}, если вызывающий — ADMIN (без ограничений на редактирование Pair); иначе ID
   * вызывающего STUDENT (JWT {@code sub} == {@code Student.id}, см. флоу регистрации), используемый
   * сервисом для проверки группы и статуса цикла.
   */
  private UUID callerStudentId(Jwt jwt, Authentication authentication) {
    boolean isAdmin = authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    return isAdmin ? null : UUID.fromString(jwt.getSubject());
  }
}
