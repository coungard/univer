package com.coungard.univer.dto.registration;

import lombok.Builder;

/**
 * Регистрационные данные для Keycloak
 *
 * @param username - логин пользователя
 * @param firstname - имя
 * @param lastname - фамилия
 * @param email - почта
 * @param password - пароль
 */
@Builder
public record RegisterData(

    String username,

    String firstname,

    String lastname,

    String email,

    String password
) {

}