package com.coungard.univer.controller;

import com.coungard.univer.dto.DepartmentDto;
import com.coungard.univer.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Departments", description = "CRUD и поиск кафедр с пагинацией")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

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
  @Operation(summary = "Получить кафедры по ID факультета с пагинацией")
  public ResponseEntity<Page<DepartmentDto>> getDepartmentsByFaculty(
      @PathVariable UUID facultyId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<DepartmentDto> departments = departmentService.getDepartmentsByFaculty(facultyId, pageable);
    return ResponseEntity.ok(departments);
  }

  @GetMapping("/university/{universityId}")
  @Operation(summary = "Получить кафедры по ID университета с пагинацией")
  public ResponseEntity<Page<DepartmentDto>> getDepartmentsByUniversity(
      @PathVariable UUID universityId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<DepartmentDto> departments = departmentService.getDepartmentsByUniversity(universityId, pageable);
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