package com.coungard.univer.controller;

import com.coungard.univer.dto.AvatarDto;
import com.coungard.univer.service.AccountService;
import com.coungard.univer.service.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
  private final AvatarService avatarService;

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

  @Operation(
      summary = "Загрузить или заменить своё фото профиля",
      description = "multipart/form-data, поле file: JPEG, PNG или WebP до 5 МБ и 8000×8000 пикселей. Формат "
          + "определяется по содержимому; HEIC не принимается — клиент перекодирует его в JPEG. Сервер учитывает "
          + "EXIF-поворот, обрезает фото по центру до квадрата и сохраняет JPEG 512×512 и 128×128 без метаданных "
          + "(геолокация удаляется). Ссылки постоянные и публичные (без токена); каждая загрузка даёт новые "
          + "ссылки, прежнее фото удаляется. Ссылки также приходят в профиле студента и преподавателя "
          + "(avatarUrl, avatarThumbnailUrl)."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Фото сохранено"),
      @ApiResponse(responseCode = "401", description = "Нет токена или он невалиден"),
      @ApiResponse(responseCode = "403", description = "Роль не STUDENT и не TEACHER"),
      @ApiResponse(responseCode = "404", description = "У пользователя нет профиля студента или преподавателя"),
      @ApiResponse(responseCode = "413", description = "Файл больше 5 МБ или изображение больше 8000×8000"),
      @ApiResponse(responseCode = "415", description = "Не изображение JPEG, PNG или WebP (в том числе HEIC)"),
      @ApiResponse(responseCode = "422", description = "Файл не передан или пустой"),
      @ApiResponse(responseCode = "429", description = "Сервер занят обработкой других фото, повторить позже")
  })
  @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  public AvatarDto uploadMyAvatar(@AuthenticationPrincipal Jwt jwt, @RequestPart("file") MultipartFile file) {
    return avatarService.upload(UUID.fromString(jwt.getSubject()), file);
  }

  @Operation(
      summary = "Удалить своё фото профиля",
      description = "Файлы удаляются, в профиле avatarUrl и avatarThumbnailUrl становятся null. Если фото нет — "
          + "тоже 204."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Фото удалено или его не было"),
      @ApiResponse(responseCode = "401", description = "Нет токена или он невалиден"),
      @ApiResponse(responseCode = "403", description = "Роль не STUDENT и не TEACHER"),
      @ApiResponse(responseCode = "404", description = "У пользователя нет профиля студента или преподавателя")
  })
  @DeleteMapping("/me/avatar")
  @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
  public ResponseEntity<Void> deleteMyAvatar(@AuthenticationPrincipal Jwt jwt) {
    avatarService.delete(UUID.fromString(jwt.getSubject()));
    return ResponseEntity.noContent().build();
  }
}
