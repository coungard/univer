package com.coungard.univer.mapper;

import com.coungard.univer.dto.PairDto;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Pair;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PairMapper {

  public PairDto toDto(Pair pair) {
    if (pair == null) {
      return null;
    }
    return PairDto.builder()
        .id(pair.getId())
        .weekScheduleCycleId(pair.getWeekScheduleCycle().getId())
        .dayOfWeek(pair.getDayOfWeek())
        .weekParity(pair.getWeekParity())
        .pairNumber(pair.getPairNumber())
        .startTime(pair.getStartTime())
        .endTime(pair.getEndTime())
        .courseId(pair.getCourse().getId())
        .teacherId(pair.getTeacher() != null ? pair.getTeacher().getId() : null)
        .groupIds(pair.getGroups().stream().map(Group::getId).collect(Collectors.toSet()))
        .build();
  }

  public Pair toEntity(PairDto dto) {
    if (dto == null) {
      return null;
    }
    Pair pair = new Pair();
    pair.setDayOfWeek(dto.dayOfWeek());
    pair.setWeekParity(dto.weekParity());
    pair.setPairNumber(dto.pairNumber());
    pair.setStartTime(dto.startTime());
    pair.setEndTime(dto.endTime());
    return pair;
  }
}
