package com.coungard.univer.mapper;

import com.coungard.univer.dto.LectureDto;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Lecture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class LectureMapper {

  public LectureDto toDto(Lecture lecture) {
    if (lecture == null) {
      return null;
    }
    return LectureDto.builder()
        .id(lecture.getId())
        .title(lecture.getTitle())
        .content(lecture.getContent())
        .scheduledTime(lecture.getScheduledTime())
        .durationMinutes(lecture.getDurationMinutes())
        .courseId(lecture.getCourse().getId())
        .teacherId(lecture.getTeacher() != null ? lecture.getTeacher().getId() : null)
        .room(lecture.getRoom())
        .sourcePairId(lecture.getSourcePair() != null ? lecture.getSourcePair().getId() : null)
        .groupIds(lecture.getGroups().stream().map(Group::getId).collect(Collectors.toSet()))
        .build();
  }

  public Lecture toEntity(LectureDto dto) {
    if (dto == null) {
      return null;
    }
    Lecture lecture = new Lecture();
    lecture.setTitle(dto.title());
    lecture.setContent(dto.content());
    lecture.setScheduledTime(dto.scheduledTime());
    lecture.setDurationMinutes(dto.durationMinutes() != null ? dto.durationMinutes() : 90);
    lecture.setRoom(dto.room());
    return lecture;
  }
}
