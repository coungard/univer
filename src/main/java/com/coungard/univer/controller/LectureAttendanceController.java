package com.coungard.univer.controller;

import com.coungard.univer.dto.AttendanceStatsDto;
import com.coungard.univer.dto.AttendanceSummaryDto;
import com.coungard.univer.dto.LectureAttendanceDto;
import com.coungard.univer.dto.MyAttendanceDto;
import com.coungard.univer.dto.request.MarkMyAttendanceRequest;
import com.coungard.univer.service.LectureAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Посещаемость лекций и статистика")
@SecurityRequirement(name = "bearerAuth")
public class LectureAttendanceController {

  private final LectureAttendanceService attendanceService;

  @Operation(
      summary = "Отметить посещение лекции",
      description = "Студент должен быть активно (ACTIVE) зачислен на курс этой лекции"
  )
  @PostMapping
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  public ResponseEntity<LectureAttendanceDto> markAttendance(@Valid @RequestBody LectureAttendanceDto attendanceDto) {
    return ResponseEntity.ok(attendanceService.markAttendance(attendanceDto));
  }

  @Operation(summary = "Получить отметки посещаемости студента с пагинацией")
  @GetMapping("/student/{studentId}")
  public ResponseEntity<Page<LectureAttendanceDto>> getAttendanceByStudent(
      @PathVariable UUID studentId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId, pageable));
  }

  @Operation(summary = "Получить отметки посещаемости лекции с пагинацией")
  @GetMapping("/lecture/{lectureId}")
  public ResponseEntity<Page<LectureAttendanceDto>> getAttendanceByLecture(
      @PathVariable UUID lectureId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(attendanceService.getAttendanceByLecture(lectureId, pageable));
  }

  @Operation(summary = "Статистика посещаемости лекции")
  @GetMapping("/lecture/{lectureId}/stats")
  public ResponseEntity<AttendanceStatsDto> getLectureStats(@PathVariable UUID lectureId) {
    return ResponseEntity.ok(attendanceService.getLectureStats(lectureId));
  }

  @Operation(summary = "Статистика посещаемости студента в рамках курса")
  @GetMapping("/student/{studentId}/course/{courseId}/stats")
  public ResponseEntity<AttendanceStatsDto> getStudentCourseStats(
      @PathVariable UUID studentId,
      @PathVariable UUID courseId) {

    return ResponseEntity.ok(attendanceService.getStudentCourseStats(studentId, courseId));
  }

  @Operation(
      summary = "Личная отметка посещения лекции",
      description = "Студент сам отмечает, был ли он на лекции своей группы (из /lectures/me). Только после "
          + "начала лекции, зачисление на курс не требуется. Возвращает итоговую отметку — отметку "
          + "преподавателя, если она есть (она главнее личной)."
  )
  @PutMapping("/me/lecture/{lectureId}")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<MyAttendanceDto> markMyAttendance(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID lectureId,
      @Valid @RequestBody MarkMyAttendanceRequest request) {

    UUID studentId = UUID.fromString(jwt.getSubject());
    return ResponseEntity.ok(attendanceService.markMyAttendance(studentId, lectureId, request.attended()));
  }

  @Operation(summary = "Снять личную отметку посещения лекции", description = "Отметку преподавателя не трогает")
  @DeleteMapping("/me/lecture/{lectureId}")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<Void> unmarkMyAttendance(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID lectureId) {
    attendanceService.unmarkMyAttendance(UUID.fromString(jwt.getSubject()), lectureId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Мои отметки посещаемости",
      description = "Итоговая отметка по каждой лекции, где есть отметка преподавателя или личная, по времени "
          + "начала лекции"
  )
  @GetMapping("/me")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<Page<MyAttendanceDto>> getMyAttendance(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    UUID studentId = UUID.fromString(jwt.getSubject());
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "scheduledTime"));
    return ResponseEntity.ok(attendanceService.getMyAttendance(studentId, pageable));
  }

  @Operation(
      summary = "Сводка моей посещаемости по курсу",
      description = "Начавшиеся лекции курса у группы студента в текущем семестре: сколько посещено, пропущено "
          + "и не отмечено. Отметка преподавателя главнее личной."
  )
  @GetMapping("/me/course/{courseId}/summary")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<AttendanceSummaryDto> getMyCourseSummary(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID courseId) {

    return ResponseEntity.ok(attendanceService.getMyCourseSummary(UUID.fromString(jwt.getSubject()), courseId));
  }
}
