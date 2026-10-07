package com.coungard.univer.service.impl;

import com.coungard.univer.dto.RegionDto;
import com.coungard.univer.mapper.RegionMapper;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.service.RegionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

  private final RegionRepository regionRepository;
  private final RegionMapper regionMapper;

  @Override
  @Transactional(readOnly = true)
  public List<RegionDto> getRegions() {
    return regionRepository.findAllByOrderByNameAsc().stream()
        .map(regionMapper::toDto)
        .toList();
  }
}
