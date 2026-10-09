package com.coungard.univer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Тело {@code POST /students/me/group}: студент создаёт группу, которой нет в справочнике, и сразу в
 * неё зачисляется. Факультет и курс в запросе не передаются — берутся из профиля студента.
 */
public record CreateStudentGroupRequest(

    @NotBlank(message = "Название группы обязательно")
    @Size(max = 64, message = "Название группы не длиннее 64 символов")
    String name,

    @Size(max = 255, message = "Расшифровка названия группы не длиннее 255 символов")
    String fullName
) {
}
