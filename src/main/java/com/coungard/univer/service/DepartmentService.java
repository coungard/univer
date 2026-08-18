package com.coungard.univer.service;

import com.coungard.univer.dto.DepartmentDto;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

  DepartmentDto createDepartment(DepartmentDto departmentDto);

  Page<DepartmentDto> getDepartmentsByFaculty(UUID facultyId, Pageable pageable);

  Page<DepartmentDto> getDepartmentsByUniversity(UUID facultyId, Pageable pageable);

  DepartmentDto getDepartmentById(UUID id);

  DepartmentDto updateDepartment(UUID id, DepartmentDto departmentDto);

  void deleteDepartment(UUID id);
}