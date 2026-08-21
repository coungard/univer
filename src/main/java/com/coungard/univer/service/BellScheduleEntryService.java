package com.coungard.univer.service;

import com.coungard.univer.dto.BellScheduleEntryDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BellScheduleEntryService {

  /**
   * Создать запись звонкового расписания. {@code universityId == null} означает системную запись
   * по умолчанию для этого номера пары — используется университетами, у которых нет собственной
   * записи. Пара (universityId, pairNumber) должна быть уникальной.
   *
   * @param entryDto данные записи
   * @return созданный BellScheduleEntryDto
   */
  BellScheduleEntryDto createEntry(BellScheduleEntryDto entryDto);

  /**
   * Получить запись по ID.
   *
   * @param id идентификатор записи
   * @return BellScheduleEntryDto
   */
  BellScheduleEntryDto getEntryById(UUID id);

  /**
   * Получить страницу всех записей с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница BellScheduleEntryDto
   */
  Page<BellScheduleEntryDto> getEntries(Pageable pageable);

  /**
   * Получить страницу записей конкретного университета.
   *
   * @param universityId идентификатор университета
   * @param pageable параметры пагинации и сортировки
   * @return страница BellScheduleEntryDto
   */
  Page<BellScheduleEntryDto> getEntriesByUniversity(UUID universityId, Pageable pageable);

  /**
   * Обновить запись звонкового расписания.
   *
   * @param id идентификатор записи
   * @param entryDto новые данные
   * @return обновлённый BellScheduleEntryDto
   */
  BellScheduleEntryDto updateEntry(UUID id, BellScheduleEntryDto entryDto);

  /**
   * Удалить запись по ID.
   *
   * @param id идентификатор записи
   */
  void deleteEntry(UUID id);
}
