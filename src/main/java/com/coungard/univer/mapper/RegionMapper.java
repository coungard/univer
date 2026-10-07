package com.coungard.univer.mapper;

import com.coungard.univer.dto.RegionDto;
import com.coungard.univer.entity.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionMapper {

  public RegionDto toDto(Region region) {
    if (region == null) {
      return null;
    }
    return RegionDto.builder()
        .id(region.getId())
        .code(region.getCode())
        .name(region.getName())
        .build();
  }
}
