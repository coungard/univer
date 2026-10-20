package com.coungard.univer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

@Builder(toBuilder = true)
public record PairDto(
    UUID id,

    @NotNull(message = "ID циклического расписания обязателен")
    UUID weekScheduleCycleId,

    @NotNull(message = "День недели обязателен")
    DayOfWeek dayOfWeek,

    @NotNull(message = "Чётность недели обязательна")
    WeekParity weekParity,

    @NotNull(message = "Номер пары обязателен")
    @Min(value = 1, message = "Номер пары не может быть меньше 1")
    Integer pairNumber,

    // Опционально: если не задано, подставляется из справочника звонкового расписания
    // (BellScheduleEntry) по университету группы и pairNumber — см. PairServiceImpl.resolveSchedule.
    LocalTime startTime,

    LocalTime endTime,

    @NotNull(message = "ID учебного курса обязателен")
    UUID courseId,

    UUID teacherId,

    // Преподаватель текстом, если его нет среди зарегистрированных (issue #104). В ответе при заданном
    // teacherId — полное имя этого Teacher; на входе при заданном teacherId игнорируется.
    @Size(max = 100, message = "Имя преподавателя не может быть длиннее 100 символов")
    String teacherName,

    String room,

    @NotEmpty(message = "Пара должна быть привязана хотя бы к одной группе")
    Set<UUID> groupIds,

    // Только в ответе (AuditableEntity, issue #119): во входящих запросах игнорируются.
    Instant createdAt,
    Instant updatedAt,
    UUID createdBy,
    UUID updatedBy
) {
}
