package com.coungard.univer.service.impl;

import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.entity.Address;
import com.coungard.univer.entity.Region;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.UniversityMapper;
import com.coungard.univer.repository.AddressRepository;
import com.coungard.univer.repository.FacultyRepository;
import com.coungard.univer.repository.FacultyRepository.UniversityFacultyCount;
import com.coungard.univer.repository.RegionRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.service.UniversityService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {

  private final UniversityRepository universityRepository;
  private final AddressRepository addressRepository;
  private final RegionRepository regionRepository;
  private final FacultyRepository facultyRepository;
  private final UniversityMapper universityMapper;

  @Override
  @Transactional(readOnly = true)
  public Page<UniversityDto> getUniversities(String search, UUID regionId, Pageable pageable) {
    boolean hasSearch = StringUtils.hasText(search);
    Page<University> page;
    if (regionId != null) {
      page = hasSearch
          ? universityRepository.findByNameContainingIgnoreCaseAndRegionId(search.trim(), regionId, pageable)
          : universityRepository.findByRegionId(regionId, pageable);
    } else {
      page = hasSearch
          ? universityRepository.findByNameContainingIgnoreCase(search.trim(), pageable)
          : universityRepository.findAll(pageable);
    }
    Map<UUID, Long> facultyCounts = countFaculties(page.getContent());
    return page.map(university ->
        universityMapper.toDto(university, facultyCounts.getOrDefault(university.getId(), 0L)));
  }

  /**
   * Число факультетов для всех университетов страницы одним запросом, а не по запросу на университет.
   */
  private Map<UUID, Long> countFaculties(List<University> universities) {
    if (universities.isEmpty()) {
      return Map.of();
    }
    List<UUID> universityIds = universities.stream().map(University::getId).toList();
    return facultyRepository.countByUniversityIds(universityIds).stream()
        .collect(Collectors.toMap(
            UniversityFacultyCount::getUniversityId, UniversityFacultyCount::getFacultyCount));
  }

  private UniversityDto toDto(University university) {
    return universityMapper.toDto(university, facultyRepository.countByUniversityId(university.getId()));
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityDto getUniversityById(UUID id) {
    University university = universityRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + id));
    return toDto(university);
  }

  @Override
  @Transactional
  public UniversityDto createUniversity(UniversityDto universityDto) {
    University university = universityMapper.toEntity(universityDto);
    university.setRegion(findRegion(universityDto.regionId()));

    University saved = universityRepository.save(university);
    return toDto(saved);
  }

  @Override
  @Transactional
  public UniversityDto updateUniversity(UUID id, UniversityDto universityDto) {
    University existing = universityRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + id));

    existing.setName(universityDto.name());
    existing.setDescription(universityDto.description());
    existing.setRector(universityDto.rector());
    existing.setFoundingYear(universityDto.foundingYear());
    existing.setStudentCount(universityDto.studentCount());

    Address address = null;

    if (universityDto.address() != null) {
      if (universityDto.address().id() != null) {
        address = addressRepository.findById(universityDto.address().id())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Address not found with id: " + universityDto.address().id()));
      } else {
        address = universityMapper.toAddressEntity(universityDto.address());
      }
    }
    existing.setAddress(address);
    existing.setRegion(findRegion(universityDto.regionId()));

    University saved = universityRepository.save(existing);
    return toDto(saved);
  }

  private Region findRegion(UUID regionId) {
    if (regionId == null) {
      throw new ValidationException("Регион обязателен");
    }
    return regionRepository.findById(regionId)
        .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + regionId));
  }

  @Override
  @Transactional
  public void deleteUniversityById(UUID id) {
    if (!universityRepository.existsById(id)) {
      throw new ResourceNotFoundException("University not found with id: " + id);
    }
    universityRepository.deleteById(id);
  }
}
