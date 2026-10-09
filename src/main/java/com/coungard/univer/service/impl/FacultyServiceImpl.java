package com.coungard.univer.service.impl;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ConflictException;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.FacultyMapper;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.service.FacultyService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    if (faculty.getName() != null) {
      faculty.setName(faculty.getName().strip());
      validateNameIsFree(university.getId(), faculty.getName(), null);
    }

    Faculty saved = facultyRepository.save(faculty);
    return facultyMapper.toDto(saved);
  }

  @Override
  @Transactional
  public FacultyDto createFacultyByStudent(UUID universityId, String name, UUID createdByStudentId) {
    University university = universityRepository.findById(universityId)
        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + universityId));

    String newName = name.strip();
    validateNameIsFree(universityId, newName, null);

    Faculty faculty = new Faculty();
    faculty.setName(newName);
    faculty.setUniversity(university);
    faculty.setCreatedByStudentId(createdByStudentId);

    Faculty saved = facultyRepository.save(faculty);
    return facultyMapper.toDto(saved);
  }

  @Override
  @Transactional
  public FacultyDto renameFaculty(UUID id, String name) {
    Faculty existing = facultyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));

    String newName = name.strip();
    validateNameIsFree(existing.getUniversity().getId(), newName, id);
    existing.setName(newName);

    Faculty updated = facultyRepository.save(existing);
    return facultyMapper.toDto(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<FacultyDto> getFacultiesByUniversity(UUID universityId, Pageable pageable) {
    return facultyRepository.findByUniversityId(universityId, pageable)
        .map(facultyMapper::toDto);
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
    existing.setName(existing.getName().strip());
    validateNameIsFree(university.getId(), existing.getName(), id);

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

  /**
   * Название факультета уникально в пределах университета без учёта регистра и крайних пробелов.
   *
   * @param selfId ID самого обновляемого факультета — он себе не мешает; {@code null} при создании
   */
  private void validateNameIsFree(UUID universityId, String name, UUID selfId) {
    facultyRepository.findByUniversityIdAndNameIgnoreCase(universityId, name).stream()
        .filter(faculty -> !faculty.getId().equals(selfId))
        .findFirst()
        .ifPresent(faculty -> {
          throw new ConflictException("name",
              "Факультет с таким названием в этом университете уже есть: " + faculty.getName(), faculty.getId());
        });
  }
}
