package com.coungard.univer.controller;

import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.dto.request.GenerateLectureRequest;
import com.coungard.univer.service.LectureService;
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
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
@Tag(name = "Lectures", description = "CRUD и поиск лекций с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class LectureController {

  private final LectureService lectureService;

  @Operation(summary = "Получить лекции с пагинацией")
  @GetMapping
  public ResponseEntity<Page<LectureDto>> getLectures(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LectureDto> lectures = lectureService.getLectures(pageable);
    return ResponseEntity.ok(lectures);
  }

  @Operation(summary = "Получить лекции по ID учебного курса с пагинацией")
  @GetMapping("/course/{courseId}")
  public ResponseEntity<Page<LectureDto>> getLecturesByCourse(
      @PathVariable UUID courseId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LectureDto> lectures = lectureService.getLecturesByCourse(courseId, pageable);
    return ResponseEntity.ok(lectures);
  }

  @Operation(summary = "Получить лекции по ID группы с пагинацией")
  @GetMapping("/group/{groupId}")
  public ResponseEntity<Page<LectureDto>> getLecturesByGroup(
      @PathVariable UUID groupId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<LectureDto> lectures = lectureService.getLecturesByGroup(groupId, pageable);
    return ResponseEntity.ok(lectures);
  }

  @Operation(
      summary = "Получить расписание текущего студента",
      description = "Возвращает лекции группы, к которой привязан вызывающий студент, "
          + "отсортированные по времени начала. ID студента берётся из JWT (Keycloak subject = "
          + "Student.id, см. флоу регистрации). Если студент ещё не привязан к группе — пустая страница."
  )
  @GetMapping("/me")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<Page<LectureDto>> getMyLectures(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    UUID studentId = UUID.fromString(jwt.getSubject());
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "scheduledTime"));
    Page<LectureDto> lectures = lectureService.getMyLectures(studentId, pageable);
    return ResponseEntity.ok(lectures);
  }

  @Operation(summary = "Получить лекцию по ID")
  @GetMapping("/{id}")
  public ResponseEntity<LectureDto> getLectureById(@PathVariable UUID id) {
    LectureDto dto = lectureService.getLectureById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать лекцию вручную")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<LectureDto> createLecture(@Valid @RequestBody LectureDto lectureDto) {
    LectureDto saved = lectureService.createLecture(lectureDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(
      summary = "Сгенерировать лекцию из шаблона Pair",
      description = "Курс, преподаватель и группы копируются из шаблона циклического расписания; "
          + "дата должна соответствовать дню недели и чётности недели пары. ADMIN/TEACHER — без "
          + "ограничений; STUDENT — только если Pair принадлежит его группе."
  )
  @PostMapping("/generate")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
  public ResponseEntity<LectureDto> generateFromPair(
      @Valid @RequestBody GenerateLectureRequest request,
      @AuthenticationPrincipal Jwt jwt,
      Authentication authentication) {

    LectureDto saved = lectureService.generateFromPair(request, callerStudentId(jwt, authentication));

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .replacePath("/api/v1/lectures/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(
      summary = "Сгенерировать лекции на весь семестр из пар цикла расписания",
      description = "Для каждой подходящей Pair цикла перебираются все подходящие по дню недели и "
          + "чётности недели даты в границах [Semester.startDate, Semester.endDate]; уже "
          + "сгенерированные пара+дата пропускаются без ошибки — операцию безопасно вызывать "
          + "повторно. ADMIN/TEACHER — по всем Pair цикла, как раньше; STUDENT — только по Pair "
          + "своей группы, остальные Pair цикла пропускаются молча."
  )
  @PostMapping("/generate/semester/{weekScheduleCycleId}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
  public ResponseEntity<List<LectureDto>> generateSemesterLectures(
      @PathVariable UUID weekScheduleCycleId,
      @AuthenticationPrincipal Jwt jwt,
      Authentication authentication) {

    List<LectureDto> generated = lectureService.generateSemesterLectures(
        weekScheduleCycleId, callerStudentId(jwt, authentication));
    return ResponseEntity.ok(generated);
  }

  @Operation(summary = "Обновить лекцию")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<LectureDto> updateLecture(
      @PathVariable UUID id,
      @Valid @RequestBody LectureDto lectureDto) {

    LectureDto updated = lectureService.updateLecture(id, lectureDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить лекцию")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<Void> deleteLecture(@PathVariable UUID id) {
    lectureService.deleteLecture(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * {@code null}, если вызывающий — ADMIN или TEACHER (без ограничений на генерацию); иначе ID
   * вызывающего STUDENT (JWT {@code sub} == {@code Student.id}, см. флоу регистрации), используемый
   * сервисом для ограничения генерации его собственной группой.
   */
  private UUID callerStudentId(Jwt jwt, Authentication authentication) {
    boolean isAdminOrTeacher = authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")
            || authority.getAuthority().equals("ROLE_TEACHER"));
    return isAdminOrTeacher ? null : UUID.fromString(jwt.getSubject());
  }
}
