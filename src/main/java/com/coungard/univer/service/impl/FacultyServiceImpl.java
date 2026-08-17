package com.coungard.univer.service.impl;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.FacultyMapper;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.service.FacultyService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {

  private final FacultyRepository facultyRepository;
  private final UniversityRepository universityRepository;
  private final FacultyMapper facultyMapper;

  @Override
  @Transactional
  public FacultyDto createFaculty(FacultyDto facultyDto) {
    University university = universityRepository.findById(facultyDto.universityId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "University not found with id: " + facultyDto.universityId()));

    Faculty faculty = facultyMapper.toEntity(facultyDto);
    faculty.setUniversity(university);

    Faculty saved = facultyRepository.save(faculty);
    return facultyMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FacultyDto> getFacultiesByUniversity(UUID universityId) {
    return facultyRepository.findByUniversityId(universityId).stream()
        .map(facultyMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public FacultyDto getFacultyById(UUID id) {
    Faculty faculty = facultyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));
    return facultyMapper.toDto(faculty);
  }

  @Override
  @Transactional
  public FacultyDto updateFaculty(UUID id, FacultyDto facultyDto) {
    Faculty existing = facultyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));

    University university = universityRepository.findById(facultyDto.universityId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "University not found with id: " + facultyDto.universityId()));

    facultyMapper.updateEntityFromDto(facultyDto, existing);
    existing.setUniversity(university);

    Faculty updated = facultyRepository.save(existing);
    return facultyMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteFaculty(UUID id) {
    if (!facultyRepository.existsById(id)) {
      throw new ResourceNotFoundException("Faculty not found with id: " + id);
    }
    facultyRepository.deleteById(id);
  }
}
