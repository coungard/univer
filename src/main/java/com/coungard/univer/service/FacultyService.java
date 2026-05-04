package com.coungard.univer.service;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.dto.mapper.FacultyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import java.util.UUID;

@Service
public class FacultyService {

  @Autowired
  private FacultyRepository facultyRepository;

  @Autowired
  private UniversityRepository universityRepository;

  @Autowired
  private FacultyMapper facultyMapper;

  public FacultyDto createFaculty(FacultyDto facultyDto) {
    University university = universityRepository.findById(facultyDto.universityId())
        .orElseThrow(() -> new ResponseStatusException(
            NOT_FOUND,
            "University not found with id: " + facultyDto.universityId()
        ));

    Faculty faculty = facultyMapper.toEntity(facultyDto);
    faculty.setUniversity(university);

    Faculty saved = facultyRepository.save(faculty);
    return facultyMapper.toDto(saved);
  }

  public List<FacultyDto> getFacultiesByUniversity(UUID universityId) {
    return facultyRepository.findByUniversityId(universityId).stream()
        .map(facultyMapper::toDto)
        .toList();
  }

  public FacultyDto getFacultyById(UUID id) {
    Faculty faculty = facultyRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            NOT_FOUND,
            "Faculty not found with id: " + id
        ));
    return facultyMapper.toDto(faculty);
  }

  public FacultyDto updateFaculty(UUID id, FacultyDto facultyDto) {
    Faculty existing = facultyRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            NOT_FOUND,
            "Faculty not found with id: " + id
        ));

    University university = universityRepository.findById(facultyDto.universityId())
        .orElseThrow(() -> new ResponseStatusException(
            NOT_FOUND,
            "University not found with id: " + facultyDto.universityId()
        ));

    facultyMapper.updateEntityFromDto(facultyDto, existing);
    existing.setUniversity(university);

    Faculty updated = facultyRepository.save(existing);
    return facultyMapper.toDto(updated);
  }

  public void deleteFaculty(UUID id) {
    if (!facultyRepository.existsById(id)) {
      throw new ResponseStatusException(NOT_FOUND, "Faculty not found with id: " + id);
    }
    facultyRepository.deleteById(id);
  }
}