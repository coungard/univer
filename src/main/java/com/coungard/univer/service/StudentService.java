package com.coungard.univer.service;

import com.coungard.univer.dto.StudentDto;
import com.coungard.univer.dto.registration.RegisterStudentRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

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
   * Удалить студента по ID. Также удаляет пользователя из Keycloak.
   *
   * @param id идентификатор студента
   */
  void deleteStudentById(UUID id);
}