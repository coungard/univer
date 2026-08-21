package com.coungard.univer.service.impl;

import com.coungard.univer.dto.BellScheduleEntryDto;
import com.coungard.univer.entity.BellScheduleEntry;
import com.coungard.univer.entity.University;
import com.coungard.univer.exception.ResourceNotFoundException;
import com.coungard.univer.exception.ValidationException;
import com.coungard.univer.mapper.BellScheduleEntryMapper;
import com.coungard.univer.repository.BellScheduleEntryRepository;
import com.coungard.univer.repository.UniversityRepository;
import com.coungard.univer.service.BellScheduleEntryService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BellScheduleEntryServiceImpl implements BellScheduleEntryService {

  private final BellScheduleEntryRepository bellScheduleEntryRepository;
  private final UniversityRepository universityRepository;
  private final BellScheduleEntryMapper bellScheduleEntryMapper;

  @Override
  @Transactional
  public BellScheduleEntryDto createEntry(BellScheduleEntryDto entryDto) {
    checkNotDuplicate(entryDto.universityId(), entryDto.pairNumber());

    BellScheduleEntry entry = bellScheduleEntryMapper.toEntity(entryDto);
    entry.setUniversity(resolveUniversity(entryDto.universityId()));

    BellScheduleEntry saved = bellScheduleEntryRepository.save(entry);
    return bellScheduleEntryMapper.toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public BellScheduleEntryDto getEntryById(UUID id) {
    BellScheduleEntry entry = bellScheduleEntryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Запись звонкового расписания не найдена с ID: " + id));
    return bellScheduleEntryMapper.toDto(entry);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BellScheduleEntryDto> getEntries(Pageable pageable) {
    return bellScheduleEntryRepository.findAll(pageable).map(bellScheduleEntryMapper::toDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BellScheduleEntryDto> getEntriesByUniversity(UUID universityId, Pageable pageable) {
    return bellScheduleEntryRepository.findByUniversityId(universityId, pageable).map(bellScheduleEntryMapper::toDto);
  }

  @Override
  @Transactional
  public BellScheduleEntryDto updateEntry(UUID id, BellScheduleEntryDto entryDto) {
    BellScheduleEntry existing = bellScheduleEntryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Запись звонкового расписания не найдена с ID: " + id));

    boolean universityChanged = !Objects.equals(entryDto.universityId(),
        existing.getUniversity() != null ? existing.getUniversity().getId() : null);
    boolean pairNumberChanged = !entryDto.pairNumber().equals(existing.getPairNumber());
    if (universityChanged || pairNumberChanged) {
      checkNotDuplicate(entryDto.universityId(), entryDto.pairNumber());
    }

    existing.setUniversity(resolveUniversity(entryDto.universityId()));
    existing.setPairNumber(entryDto.pairNumber());
    existing.setStartTime(entryDto.startTime());
    existing.setEndTime(entryDto.endTime());

    BellScheduleEntry updated = bellScheduleEntryRepository.save(existing);
    return bellScheduleEntryMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void deleteEntry(UUID id) {
    if (!bellScheduleEntryRepository.existsById(id)) {
      throw new ResourceNotFoundException("Запись звонкового расписания не найдена с ID: " + id);
    }
    bellScheduleEntryRepository.deleteById(id);
  }

  private void checkNotDuplicate(UUID universityId, Integer pairNumber) {
    boolean duplicate = universityId != null
        ? bellScheduleEntryRepository.existsByUniversityIdAndPairNumber(universityId, pairNumber)
        : bellScheduleEntryRepository.existsByUniversityIsNullAndPairNumber(pairNumber);
    if (duplicate) {
      throw new ValidationException(
          "Запись звонкового расписания для номера пары " + pairNumber
              + (universityId != null ? " и этого университета" : " по умолчанию") + " уже существует");
    }
  }

  private University resolveUniversity(UUID universityId) {
    if (universityId == null) {
      return null;
    }
    return universityRepository.findById(universityId)
        .orElseThrow(() -> new ResourceNotFoundException("Университет не найден с ID: " + universityId));
  }
}
