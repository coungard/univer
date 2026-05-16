package com.coungard.univer.dto.request;

import com.coungard.univer.dto.EducationForm;
import com.coungard.univer.dto.StudyDuration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateProgramRequest(

    @NotNull(message = "ID факультета обязателен")
    UUID facultyId,

    @NotBlank(message = "Код программы обязателен")
    String code,

    @NotBlank(message = "Наименование программы обязательно")
    String name,

    String profession,
    String direction,

    @NotBlank(message = "Уровень образования обязателен")
    String educationLevel,

    EducationForm educationForm,

    @NotNull(message = "Срок обучения обязателен")
    StudyDuration durationOfStudy,

    String qualification
) {

}