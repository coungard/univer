package com.coungard.univer.mapper;

import com.coungard.univer.dto.WeekScheduleCycleDto;
import com.coungard.univer.entity.WeekScheduleCycle;
import org.springframework.stereotype.Component;

@Component
public class WeekScheduleCycleMapper {

  public WeekScheduleCycleDto toDto(WeekScheduleCycle cycle) {
    if (cycle == null) {
      return null;
    }
    return WeekScheduleCycleDto.builder()
        .id(cycle.getId())
        .semesterId(cycle.getSemester().getId())
        .status(cycle.getStatus())
        .build();
  }

  public WeekScheduleCycle toEntity(WeekScheduleCycleDto dto) {
    if (dto == null) {
      return null;
    }
    return new WeekScheduleCycle();
  }
}
