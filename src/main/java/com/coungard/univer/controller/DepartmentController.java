package com.coungard.univer.controller;

import com.coungard.univer.dto.DepartmentDto;
import com.coungard.univer.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Departments", description = "CRUD операции для кафедр")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "Добавить кафедру")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        DepartmentDto created = departmentService.createDepartment(departmentDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/faculty/{facultyId}")
    @Operation(summary = "Получить кафедры по ID факультета")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsByFaculty(@PathVariable UUID facultyId) {
        List<DepartmentDto> departments = departmentService.getDepartmentsByFaculty(facultyId);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/university/{universityId}")
    @Operation(summary = "Получить кафедры по ID университета")
    public ResponseEntity<List<DepartmentDto>> getDepartmentsByUniversity(@PathVariable UUID universityId) {
        List<DepartmentDto> departments = departmentService.getDepartmentsByUniversity(universityId);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить кафедру по ID")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable UUID id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить кафедру по ID")
    public ResponseEntity<DepartmentDto> updateDepartment(
            @PathVariable UUID id,
            @RequestBody DepartmentDto departmentDto) {
        DepartmentDto updated = departmentService.updateDepartment(id, departmentDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить кафедру по ID")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}