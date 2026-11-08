package com.coungard.univer.controller;

import com.coungard.univer.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Аккаунт текущего пользователя независимо от роли (issue #133).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Аккаунт текущего пользователя")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final AccountService accountService;

  @Operation(
      summary = "Удалить свой аккаунт",
      description = "Сразу и безвозвратно: профиль студента или преподавателя, логин в Keycloak вместе с "
          + "сессиями и refresh-токенами. У студента удаляются записи на курсы, посещаемость и заявки на "
          + "вуз; у преподавателя пары, лекции и предметы остаются без преподавателя. Отзывы обезличиваются. "
          + "Пользователь берётся из токена, тело не нужно. Повторный вызов — тоже 204."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Аккаунт удалён"),
      @ApiResponse(responseCode = "401", description = "Нет токена или он невалиден"),
      @ApiResponse(responseCode = "403", description = "Роль не STUDENT и не TEACHER"),
      @ApiResponse(responseCode = "500", description = "Keycloak недоступен; данные не изменены, можно повторить")
  })
  @DeleteMapping("/me")
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal Jwt jwt) {
    accountService.deleteAccount(UUID.fromString(jwt.getSubject()));
    return ResponseEntity.noContent().build();
  }
}
