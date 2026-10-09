package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Тело {@code POST} и {@code PUT /students/me/faculty}: студент создаёт факультет, которого нет в
 * справочнике, либо исправляет название созданного им. Университет в запросе не передаётся — берётся
 * из профиля студента.
 */
public record CreateStudentFacultyRequest(

    @NotBlank(message = "Название факультета обязательно")
    @Size(max = 255, message = "Название факультета не длиннее 255 символов")
    String name
) {
}
