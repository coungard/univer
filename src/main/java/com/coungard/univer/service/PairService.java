package com.coungard.univer.service;

import com.coungard.univer.dto.PairDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PairService {

  /**
   * Создать пару в циклическом расписании. Обязательна привязка к циклу, учебному курсу и хотя бы
   * одной группе (может быть несколько — поточная лекция). Преподаватель опционален.
   *
   * @param pairDto         данные пары
   * @param callerStudentId {@code null}, если вызывает ADMIN (без ограничений); иначе ID
   *                        вызывающего STUDENT — пара должна принадлежать его группе, а цикл
   *                        расписания быть в статусе DRAFT
   * @return созданный PairDto
   */
  PairDto createPair(PairDto pairDto, UUID callerStudentId);

  /**
   * Получить пару по ID.
   *
   * @param id идентификатор пары
   * @return PairDto
   */
  PairDto getPairById(UUID id);

  /**
   * Получить страницу пар с пагинацией.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница PairDto
   */
  Page<PairDto> getPairs(Pageable pageable);

  /**
   * Получить страницу пар по ID циклического расписания.
   *
   * @param weekScheduleCycleId идентификатор цикла
   * @param pageable параметры пагинации и сортировки
   * @return страница PairDto
   */
  Page<PairDto> getPairsByWeekScheduleCycle(UUID weekScheduleCycleId, Pageable pageable);

  /**
   * Получить расписание конкретной группы — производная выборка всех пар, где среди связанных
   * групп есть указанная (напрямую или как участник потока).
   *
   * @param groupId идентификатор группы
   * @param pageable параметры пагинации и сортировки
   * @return страница PairDto
   */
  Page<PairDto> getPairsByGroup(UUID groupId, Pageable pageable);

  /**
   * Обновить пару.
   *
   * @param id              идентификатор пары
   * @param pairDto         новые данные
   * @param callerStudentId {@code null}, если вызывает ADMIN; иначе ID вызывающего STUDENT — и
   *                        текущее, и новое состояние пары (группы, цикл) должны проходить проверку
   *                        (своя группа + цикл в DRAFT), т.к. dto может переносить пару в другой
   *                        цикл/группу
   * @return обновлённый PairDto
   */
  PairDto updatePair(UUID id, PairDto pairDto, UUID callerStudentId);

  /**
   * Удалить пару по ID.
   *
   * @param id              идентификатор пары
   * @param callerStudentId {@code null}, если вызывает ADMIN; иначе ID вызывающего STUDENT — пара
   *                        должна принадлежать его группе, а цикл — быть в статусе DRAFT
   */
  void deletePair(UUID id, UUID callerStudentId);
}
