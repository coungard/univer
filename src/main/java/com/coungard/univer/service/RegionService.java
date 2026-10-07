package com.coungard.univer.service;

import com.coungard.univer.dto.RegionDto;
import java.util.List;

public interface RegionService {

  /**
   * Получить все регионы (субъекты РФ), отсортированные по названию
   */
  List<RegionDto> getRegions();
}
