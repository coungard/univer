package com.coungard.univer.service;

import com.coungard.univer.dto.UniversityRequestDto;
import com.coungard.univer.dto.UniversityRequestStatus;
import com.coungard.univer.dto.request.SubmitUniversityRequest;
import com.coungard.univer.entity.Student;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Заявки студентов на добавление университета, которого нет в справочнике.
 */
public interface UniversityRequestService {

  /**
   * Оставить заявку самим студентом либо обновить свою необработанную: у студента не больше одной
   * заявки в {@code PENDING}. После закрытия или отклонения прежней создаётся новая.
   *
   * @param studentId идентификатор студента (Keycloak subject вызывающего)
   * @param request название университета и необязательный регион
   * @return созданная или обновлённая заявка
   * @throws com.coungard.univer.exception.ValidationException у студента уже выбран университет
   * @throws com.coungard.univer.exception.ResourceNotFoundException регион не найден
   */
  UniversityRequestDto submitMyRequest(UUID studentId, SubmitUniversityRequest request);

  /**
   * Получить последнюю заявку студента — необработанную, а если её нет, то последнюю закрытую или
   * отклонённую.
   *
   * @param studentId идентификатор студента (Keycloak subject вызывающего)
   * @throws com.coungard.univer.exception.ResourceNotFoundException студент заявок не оставлял
   */
  UniversityRequestDto getMyRequest(UUID studentId);

  /**
   * Получить страницу заявок для администратора.
   *
   * @param status фильтр по статусу; {@code null} — заявки во всех статусах
   */
  Page<UniversityRequestDto> getRequests(UniversityRequestStatus status, Pageable pageable);

  /**
   * Закрыть заявку администратором с указанием университета. Университет проставляется в профиль
   * автора, только если тот всё ещё пуст.
   *
   * @param id идентификатор заявки
   * @param universityId добавленный или уже существовавший университет
   * @throws com.coungard.univer.exception.ValidationException заявка уже обработана
   */
  UniversityRequestDto completeRequest(UUID id, UUID universityId);

  /**
   * Отклонить заявку администратором. Профиль автора не меняется.
   *
   * @param id идентификатор заявки
   * @throws com.coungard.univer.exception.ValidationException заявка уже обработана
   */
  UniversityRequestDto rejectRequest(UUID id);

  /**
   * Закрыть необработанную заявку студента, у которого появился университет не через заявку: он сам
   * выбрал его в профиле либо его назначил администратор. Заявка закрывается этим университетом. Если
   * университета у студента нет или необработанной заявки нет — ничего не происходит.
   *
   * @param student студент с уже обновлённым профилем
   */
  void completePendingRequestOnUniversityChosen(Student student);
}
