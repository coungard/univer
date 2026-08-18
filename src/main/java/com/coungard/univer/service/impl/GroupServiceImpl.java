package com.coungard.univer.service.impl;

import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.entity.Group;
import com.coungard.univer.entity.Semester;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.GroupMapper;
import com.coungard.univer.repository.GroupRepository;
import com.coungard.univer.repository.SemesterRepository;
import com.coungard.univer.service.GroupService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

  private final GroupRepository groupRepository;
  private final SemesterRepository semesterRepository;
  private final GroupMapper groupMapper;

  @Override
  @Transactional
  public GroupDto createGroup(GroupDto groupDto) {
    Semester semester = semesterRepository.findById(groupDto.semesterId())
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + groupDto.semesterId()));

    Group group = groupMapper.toEntity(groupDto);
    group.setSemester(semester);

    Group saved = groupRepository.save(group);
    return groupMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public GroupDto getGroupById(UUID id) {
    Group group = groupRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + id));
    return groupMapper.toDto(group);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GroupDto> getGroups(Pageable pageable) {
    return groupRepository.findAll(pageable).map(groupMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<GroupDto> getGroupsBySemester(UUID semesterId, Pageable pageable) {
    return groupRepository.findBySemesterId(semesterId, pageable).map(groupMapper::toDto);
  }

  @Override
  @Transactional
  public GroupDto updateGroup(UUID id, GroupDto groupDto) {
    Group existing = groupRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена с ID: " + id));

    Semester semester = semesterRepository.findById(groupDto.semesterId())
        .orElseThrow(() -> new ResourceNotFoundException("Семестр не найден с ID: " + groupDto.semesterId()));

    existing.setSemester(semester);
    existing.setName(groupDto.name());

    Group updated = groupRepository.save(existing);
    return groupMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteGroup(UUID id) {
    if (!groupRepository.existsById(id)) {
      throw new ResourceNotFoundException("Группа не найдена с ID: " + id);
    }
    groupRepository.deleteById(id);
  }
}
