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
   * Получить страницу групп факультета и/или курса — по всем программам факультета, только из
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
