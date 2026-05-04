package com.coungard.univer.service.impl;

import com.coungard.univer.dto.DepartmentDto;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.entity.Department;
import com.coungard.univer.repository.DepartmentRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private FacultyRepository facultyRepository;

  @Override
  public DepartmentDto createDepartment(DepartmentDto departmentDto) {
    if (departmentRepository.existsByNameAndFacultyId(departmentDto.name(), departmentDto.facultyId())) {
      throw new IllegalArgumentException("Кафедра с таким названием уже существует в этом факультете");
    }
    Faculty faculty = facultyRepository.findById(departmentDto.facultyId()).orElseThrow(
        () -> new ResourceNotFoundException("Факультет c Id:" + departmentDto.facultyId() + "не найдеен")
    );
    Department department = new Department();
    department.setName(departmentDto.name());
    department.setDescription(departmentDto.description());
    department.setFaculty(faculty);

    Department saved = departmentRepository.save(department);
    return toDto(saved);
  }

  @Override
  public List<DepartmentDto> getDepartmentsByFaculty(UUID facultyId) {
    return departmentRepository.findByFacultyId(facultyId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<DepartmentDto> getDepartmentsByUniversity(UUID universityId) {
    return departmentRepository.findByUniversityId(universityId).stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public DepartmentDto getDepartmentById(UUID id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Кафедра не найдена с ID: " + id));
    return toDto(department);
  }

  @Override
  public DepartmentDto updateDepartment(UUID id, DepartmentDto departmentDto) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Кафедра не найдена с ID: " + id));

    if (!department.getName().equals(departmentDto.name()) ||
        !departmentDto.facultyId().equals(department.getFaculty().getId())) {
      if (departmentRepository.existsByNameAndFacultyId(departmentDto.name(), departmentDto.facultyId())) {
        throw new IllegalArgumentException("Кафедра с таким названием уже существует в этом факультете");
      }
    }

    department.setName(departmentDto.name());
    department.setDescription(departmentDto.description());

    Department updated = departmentRepository.save(department);
    return toDto(updated);
  }

  @Override
  public void deleteDepartment(UUID id) {
    if (!departmentRepository.existsById(id)) {
      throw new ResourceNotFoundException("Кафедра с ID: " + id + " не найдена");
    }
    departmentRepository.deleteById(id);
  }

  private DepartmentDto toDto(Department department) {
    return new DepartmentDto(
        department.getId(),
        department.getName(),
        department.getDescription(),
        department.getFaculty().getId()
    );
  }
}