package com.coungard.univer.controller;

import com.coungard.univer.dto.RegionDto;
import com.coungard.univer.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
@Tag(name = "Regions", description = "Справочник регионов (субъектов РФ)")
public class RegionController {

  private final RegionService regionService;

  @GetMapping
  @Operation(summary = "Получить все регионы, отсортированные по названию")
  public ResponseEntity<List<RegionDto>> getRegions() {
    return ResponseEntity.ok(regionService.getRegions());
  }
}
