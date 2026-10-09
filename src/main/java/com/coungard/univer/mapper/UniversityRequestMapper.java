package com.coungard.univer.mapper;

import com.coungard.univer.dto.UniversityRequestDto;
import com.coungard.univer.entity.UniversityRequest;
import org.springframework.stereotype.Component;

@Component
public class UniversityRequestMapper {

  public UniversityRequestDto toDto(UniversityRequest request) {
    if (request == null) {
      return null;
    }
    return UniversityRequestDto.builder()
        .id(request.getId())
        .name(request.getName())
        .regionId(request.getRegion() != null ? request.getRegion().getId() : null)
        .regionName(request.getRegion() != null ? request.getRegion().getName() : null)
        .status(request.getStatus())
        .universityId(request.getUniversity() != null ? request.getUniversity().getId() : null)
        .studentId(request.getStudent().getId())
        .studentUsername(request.getStudent().getPerson().getUsername())
        .studentFullname(request.getStudent().getPerson().getFullname())
        .createdAt(request.getCreatedAt())
        .updatedAt(request.getUpdatedAt())
        .build();
  }
}
