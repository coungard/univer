package com.coungard.univer.service;

import com.coungard.univer.dto.WeekScheduleCycleDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WeekScheduleCycleService {

  /**
   * Создать циклическое расписание семестра. Один цикл на семестр — повторное создание для уже
   * занятого семестра запрещено.
   *
   * @param cycleDto данные цикла
   * @return созданный WeekScheduleCycleDto
   */
  WeekScheduleCycleDto createWeekScheduleCycle(WeekScheduleCycleDto cycleDto);

  /**
   * Получить цикл по ID.
   *
   * @param id идентификатор цикла
   * @return WeekScheduleCycleDto
   */
  WeekScheduleCycleDto getWeekScheduleCycleById(UUID id);

  /**
   * Получить цикл по ID семестра.
   *
   * @param semesterId идентификатор семестра
   * @return WeekScheduleCycleDto
   */
  WeekScheduleCycleDto getWeekScheduleCycleBySemester(UUID semesterId);

  /**
   * Получить страницу циклов с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница WeekScheduleCycleDto
   */
  Page<WeekScheduleCycleDto> getWeekScheduleCycles(Pageable pageable);

  /**
   * Удалить цикл по ID. Каскадно удаляет все связанные {@code Pair}.
   *
   * @param id идентификатор цикла
   */
  void deleteWeekScheduleCycle(UUID id);
}
