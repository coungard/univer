package com.coungard.univer.service;

import com.coungard.univer.dto.GroupDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupService {

  /**
   * Создать новую студенческую группу. Обязательна привязка к семестру.
   *
   * @param groupDto данные группы
   * @return созданный GroupDto
   */
  GroupDto createGroup(GroupDto groupDto);

  /**
   * Создать группу на курсе факультета, не зная семестра: группа попадает в актуальный семестр
   * учебного года — тот же, из которого группы отдаёт {@link #getGroups(UUID, Integer, Pageable)},
   * поэтому сразу видна в этом списке. Если учебного года {@code (facultyId, yearNumber)} или
   * семестров у него ещё нет — они создаются; даты нового семестра — см. {@link SemesterPeriod}.
   *
   * @param facultyId идентификатор факультета
   * @param yearNumber номер курса
   * @param name название группы
   * @param fullName полная расшифровка названия, необязательна
   * @param createdByStudentId студент, создающий группу
   * @return созданный GroupDto
   * @throws com.coungard.univer.exception.ConflictException группа с таким названием в этом семестре
   *     уже есть — с ID существующей группы
   */
  GroupDto createGroupInCurrentSemester(UUID facultyId, Integer yearNumber, String name, String fullName,
      UUID createdByStudentId);

  /**
   * Изменить название группы и его расшифровку, не трогая семестр и создателя.
   *
   * @param id идентификатор группы
   * @param name новое название
   * @param fullName новая расшифровка названия; {@code null} или пустая строка её очищает
   * @return обновлённый GroupDto
   * @throws com.coungard.univer.exception.ConflictException другая группа с таким названием в этом
   *     семестре уже есть — с ID существующей группы
   */
  GroupDto renameGroup(UUID id, String name, String fullName);

  /**
   * Получить группу по ID.
   *
   * @param id идентификатор группы
   * @return GroupDto
   */
  GroupDto getGroupById(UUID id);

  /**
   * Получить страницу групп с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница GroupDto
   */
  Page<GroupDto> getGroups(Pageable pageable);

  /**
   * Получить страницу групп факультета и/или курса — только из
   * актуального семестра каждого учебного года: идущего сейчас, иначе ближайшего будущего, иначе
   * последнего закончившегося. Если оба фильтра не заданы — все группы, как {@link #getGroups(Pageable)}.
   *
   * @param facultyId идентификатор факультета, необязателен
   * @param yearNumber номер курса, необязателен
   * @param pageable параметры пагинации
   * @return страница GroupDto; пустая, если подходящих групп нет
   */
  Page<GroupDto> getGroups(UUID facultyId, Integer yearNumber, Pageable pageable);

  /**
   * Получить страницу групп по ID семестра.
   *
   * @param semesterId идентификатор семестра
   * @param pageable параметры пагинации и сортировки
   * @return страница GroupDto
   */
  Page<GroupDto> getGroupsBySemester(UUID semesterId, Pageable pageable);

  /**
   * Обновить группу.
   *
   * @param id идентификатор группы
   * @param groupDto новые данные
   * @return обновлённый GroupDto
   */
  GroupDto updateGroup(UUID id, GroupDto groupDto);

  /**
   * Удалить группу по ID.
   *
   * @param id идентификатор группы
   */
  void deleteGroup(UUID id);
}
