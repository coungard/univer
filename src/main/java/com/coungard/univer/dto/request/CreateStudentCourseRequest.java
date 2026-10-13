package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Тело {@code POST /students/me/course}: студент создаёт предмет, которого нет в списке предметов
 * его факультета. Факультет в запросе не передаётся — берётся из профиля студента.
 */
public record CreateStudentCourseRequest(

    @NotBlank(message = "Название предмета обязательно")
    @Size(max = 255, message = "Название предмета не длиннее 255 символов")
    String title
) {
}
