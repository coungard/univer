package com.coungard.univer.mapper;

import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

  public GroupDto toDto(Group group) {
    if (group == null) {
      return null;
    }
    return GroupDto.builder()
        .id(group.getId())
        .semesterId(group.getSemester().getId())
        .name(group.getName())
        .build();
  }

  public Group toEntity(GroupDto dto) {
    if (dto == null) {
      return null;
    }
    Group group = new Group();
    group.setName(dto.name());
    return group;
  }
}
