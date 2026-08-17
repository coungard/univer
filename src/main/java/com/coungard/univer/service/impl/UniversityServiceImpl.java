package com.coungard.univer.service.impl;

import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.entity.Address;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.mapper.UniversityMapper;
import com.coungard.univer.repository.AddressRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.service.UniversityService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {

  private final UniversityRepository universityRepository;
  private final AddressRepository addressRepository;
  private final UniversityMapper universityMapper;

  @Override
  @Transactional(readOnly = true)
  public List<UniversityDto> getAllUniversities() {
    return universityMapper.toDtoList(universityRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public UniversityDto getUniversityById(UUID id) {
    University university = universityRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + id));
    return universityMapper.toDto(university);
  }

  @Override
  @Transactional
  public UniversityDto createUniversity(UniversityDto universityDto) {
    University university = universityMapper.toEntity(universityDto);

    University saved = universityRepository.save(university);
    return universityMapper.toDto(saved);
  }

  @Override
  @Transactional
  public UniversityDto updateUniversity(UUID id, UniversityDto universityDto) {
    University existing = universityRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("University not found with id: " + id));

    existing.setName(universityDto.name());
    existing.setDescription(universityDto.description());

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

    University saved = universityRepository.save(existing);
    return universityMapper.toDto(saved);
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
