package com.coungard.univer.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpdateStudentProfileRequestTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldDistinguishMissingFieldFromExplicitNull() throws Exception {
    UUID universityId = UUID.randomUUID();
    String json = "{\"universityId\": \"" + universityId + "\", \"groupId\": null}";

    UpdateStudentProfileRequest request = objectMapper.readValue(json, UpdateStudentProfileRequest.class);

    assertThat(request.isUniversityIdSet()).isTrue();
    assertThat(request.getUniversityId()).isEqualTo(universityId);
    // Явный null — поле передано, его нужно очистить
    assertThat(request.isGroupIdSet()).isTrue();
    assertThat(request.getGroupId()).isNull();
    // Полей нет в запросе — их не трогаем
    assertThat(request.isFacultyIdSet()).isFalse();
    assertThat(request.isYearNumberSet()).isFalse();
  }

  @Test
  void shouldNotExposePresenceFlagsInJson() throws Exception {
    UpdateStudentProfileRequest request = new UpdateStudentProfileRequest();
    request.setYearNumber(2);

    String json = objectMapper.writeValueAsString(request);

    assertThat(json).contains("\"yearNumber\":2").doesNotContain("Set");
  }
}
