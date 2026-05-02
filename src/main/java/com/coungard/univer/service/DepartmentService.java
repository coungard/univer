package com.coungard.univer.service;

import com.coungard.univer.dto.DepartmentDto;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    DepartmentDto createDepartment(DepartmentDto departmentDto);

    List<DepartmentDto> getDepartmentsByFaculty(UUID facultyId);

    List<DepartmentDto> getDepartmentsByUniversity(UUID facultyId);

    DepartmentDto getDepartmentById(UUID id);

    DepartmentDto updateDepartment(UUID id, DepartmentDto departmentDto);

    void deleteDepartment(UUID id);
}