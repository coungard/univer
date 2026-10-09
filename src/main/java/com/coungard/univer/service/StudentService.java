package com.coungard.univer.service;

import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.dto.GroupDto;
import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import com.coungard.univer.dto.request.CreateStudentFacultyRequest;
import com.coungard.univer.dto.request.CreateStudentGroupRequest;
import com.coungard.univer.dto.request.UpdateStudentProfileRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

  /**
   * Сколько групп один студент может создать сам. Считаются существующие группы: удалённая
   * администратором группа лимит освобождает.
   */
  int MAX_CREATED_GROUPS = 3;

  /**
   * Сколько факультетов один студент может создать сам — по тому же правилу, что и
   * {@link #MAX_CREATED_GROUPS}.
   */
  int MAX_CREATED_FACULTIES = 3;

  /**
   * Получить страницу студентов с пагинацией и сортировкой.
   *
   * @param pageable параметры пагинации и сортировки
   * @return страница StudentDto
   */
  Page<StudentDto> getStudents(Pageable pageable);

  /**
   * Получить студента по ID.
   *
   * @param id идентификатор студента
   * @return StudentDto
   */
  StudentDto getStudentById(UUID id);

  /**
   * Зарегистрировать нового студента. Создаёт пользователя в Keycloak, назначает роль STUDENT и сохраняет в БД.
   *
   * @param registerStudentRequest данные для регистрации
   * @return StudentDto созданного студента
   */
  StudentDto registerStudent(RegisterStudentRequest registerStudentRequest);

  /**
   * Обновить данные студента.
   *
   * @param id идентификатор студента
   * @param studentDto новые данные
   * @return обновлённый StudentDto
   */
  StudentDto updateStudent(UUID id, StudentDto studentDto);

  /**
   * Частично обновить профиль самим студентом: университет, факультет, курс и группу. Меняются
   * только переданные поля; явный {@code null} очищает поле.
   *
   * <p>Поля образуют цепочку университет → факультет → курс → группа. Смена поля сбрасывает всё, что
   * ниже по цепочке (если оно не передано в том же запросе). Незаполненные поля выше по цепочке
   * проставляются по выбранному значению (например, факультет и курс — по группе), а уже заполненные
   * обязаны с ним согласовываться, иначе {@code ValidationException}.
   *
   * @param id идентификатор студента (Keycloak subject вызывающего)
   * @param request изменяемые поля
   * @return обновлённый StudentDto
   */
  StudentDto updateMyProfile(UUID id, UpdateStudentProfileRequest request);

  /**
   * Создать факультет самим студентом и сразу выбрать его в профиле — когда нужного факультета в
   * университете студента нет. Университет берётся из профиля; если он не заполнен —
   * {@code ValidationException}. Курс и группа в профиле сбрасываются, как при смене факультета в
   * {@link #updateMyProfile}.
   *
   * @param id идентификатор студента (Keycloak subject вызывающего)
   * @param request название факультета
   * @return обновлённый StudentDto с проставленным {@code facultyId}
   * @throws com.coungard.univer.exception.ValidationException студент уже создал максимум факультетов
   *     ({@link #MAX_CREATED_FACULTIES})
   */
  StudentDto createMyFaculty(UUID id, CreateStudentFacultyRequest request);

  /**
   * Исправить самим студентом название своего факультета — например, опечатку. Разрешено, только
   * если студент сам создал факультет, выбранный в его профиле, и кроме него этот факультет никто
   * из студентов не выбрал. Иначе — {@code ValidationException}.
   *
   * @param id идентификатор студента (Keycloak subject вызывающего)
   * @param request новое название факультета
   * @return обновлённый FacultyDto
   */
  FacultyDto updateMyFaculty(UUID id, CreateStudentFacultyRequest request);

  /**
   * Создать группу самим студентом и сразу зачислить его в неё — когда нужной группы на его курсе
   * нет. Факультет и курс берутся из профиля студента; если они не заполнены —
   * {@code ValidationException}. Семестр подбирается на сервере, см.
   * {@link GroupService#createGroupInCurrentSemester}.
   *
   * @param id идентификатор студента (Keycloak subject вызывающего)
   * @param request название группы и его необязательная расшифровка
   * @return обновлённый StudentDto с проставленным {@code groupId}
   * @throws com.coungard.univer.exception.ValidationException студент уже создал максимум групп
   *     ({@link #MAX_CREATED_GROUPS})
   */
  StudentDto createMyGroup(UUID id, CreateStudentGroupRequest request);

  /**
   * Исправить самим студентом название своей группы (и его расшифровку) — например, опечатку.
   * Разрешено, только если студент сам создал группу, в которой состоит, и кроме него в ней никого
   * нет: название, которое уже видят одногруппники, меняет только администратор. Иначе —
   * {@code ValidationException}.
   *
   * @param id идентификатор студента (Keycloak subject вызывающего)
   * @param request новое название группы и его необязательная расшифровка
   * @return обновлённый GroupDto
   */
  GroupDto updateMyGroup(UUID id, CreateStudentGroupRequest request);

  /**
   * Удалить студента по ID. Также удаляет пользователя из Keycloak.
   *
   * @param id идентификатор студента
   */
  void deleteStudentById(UUID id);
}