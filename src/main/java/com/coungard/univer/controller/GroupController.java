package com.coungard.univer.controller;

import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "CRUD и поиск студенческих групп с пагинацией")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

  private final GroupService groupService;

  @Operation(summary = "Получить группы с пагинацией")
  @GetMapping
  public ResponseEntity<Page<GroupDto>> getGroups(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<GroupDto> groups = groupService.getGroups(pageable);
    return ResponseEntity.ok(groups);
  }

  @Operation(summary = "Получить группы по ID семестра с пагинацией")
  @GetMapping("/semester/{semesterId}")
  public ResponseEntity<Page<GroupDto>> getGroupsBySemester(
      @PathVariable UUID semesterId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<GroupDto> groups = groupService.getGroupsBySemester(semesterId, pageable);
    return ResponseEntity.ok(groups);
  }

  @Operation(summary = "Получить группу по ID")
  @GetMapping("/{id}")
  public ResponseEntity<GroupDto> getGroupById(@PathVariable UUID id) {
    GroupDto dto = groupService.getGroupById(id);
    return ResponseEntity.ok(dto);
  }

  @Operation(summary = "Создать группу")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupDto groupDto) {
    GroupDto saved = groupService.createGroup(groupDto);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.id())
        .toUri();

    return ResponseEntity.created(location).body(saved);
  }

  @Operation(summary = "Обновить группу")
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GroupDto> updateGroup(
      @PathVariable UUID id,
      @Valid @RequestBody GroupDto groupDto) {

    GroupDto updated = groupService.updateGroup(id, groupDto);
    return ResponseEntity.ok(updated);
  }

  @Operation(summary = "Удалить группу")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
    groupService.deleteGroup(id);
    return ResponseEntity.noContent().build();
  }
}
