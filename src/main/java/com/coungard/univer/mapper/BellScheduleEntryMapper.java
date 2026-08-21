package com.coungard.univer.mapper;

import com.coungard.univer.dto.BellScheduleEntryDto;
import com.coungard.univer.entity.BellScheduleEntry;
import org.springframework.stereotype.Component;

@Component
public class BellScheduleEntryMapper {

  public BellScheduleEntryDto toDto(BellScheduleEntry entry) {
    if (entry == null) {
      return null;
    }
    return BellScheduleEntryDto.builder()
        .id(entry.getId())
        .universityId(entry.getUniversity() != null ? entry.getUniversity().getId() : null)
        .pairNumber(entry.getPairNumber())
        .startTime(entry.getStartTime())
        .endTime(entry.getEndTime())
        .build();
  }

  public BellScheduleEntry toEntity(BellScheduleEntryDto dto) {
    if (dto == null) {
      return null;
    }
    BellScheduleEntry entry = new BellScheduleEntry();
    entry.setPairNumber(dto.pairNumber());
    entry.setStartTime(dto.startTime());
    entry.setEndTime(dto.endTime());
    return entry;
  }
}
