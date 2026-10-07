package com.coungard.univer.security;

import com.coungard.univer.config.KeycloakConfig;
import com.coungard.univer.dto.registration.RegisterData;
import com.coungard.univer.exception.ConflictException;
import java.util.List;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdminService {

  private final KeycloakConfig keycloakConfig;

  public KeycloakAdminService(KeycloakConfig keycloakConfig) {
    this.keycloakConfig = keycloakConfig;
  }

  public String createUser(RegisterData registerData) {
    Keycloak keycloak = getKeycloakAdminClient();

    UserRepresentation user = new UserRepresentation();
    user.setEnabled(true);
    user.setUsername(registerData.username());
    user.setFirstName(registerData.firstname());
    user.setLastName(registerData.lastname());
    user.setEmail(registerData.email());

    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(registerData.password());
    credential.setTemporary(false);

    user.setCredentials(List.of(credential));

    var response = keycloak.realm(keycloakConfig.getRealm()).users().create(user);
    if (response.getStatus() == 409) {
      // Локальная проверка занятости смотрит только свою таблицу (студенты или преподаватели), а в
      // Keycloak логин и email общие — например, преподаватель с email уже существующего студента.
      // Keycloak отвечает {"errorMessage":"User exists with same username"} либо "...same email".
      String error = response.readEntity(String.class);
      if (error != null && error.contains("username")) {
        throw new ConflictException("username", "Пользователь с таким логином уже существует: "
            + registerData.username());
      }
      if (error != null && error.contains("email")) {
        throw new ConflictException("email", "Пользователь с таким email уже существует: "
            + registerData.email());
      }
      throw new ConflictException(null, "Пользователь с таким логином или email уже существует");
    }
    String locationHeader = response.getLocation().toString();
    return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
  }

  public void deleteUser(String userId) {
    try (Keycloak keycloak = getKeycloakAdminClient()) {
      keycloak.realm(keycloakConfig.getRealm())
          .users()
          .get(userId)
          .remove();
    }
  }

  private Keycloak getKeycloakAdminClient() {
    return KeycloakBuilder.builder()
        .serverUrl(keycloakConfig.getAuthServerUrl())
        .realm("master")
        .username(keycloakConfig.getAdminUsername())
        .password(keycloakConfig.getAdminPassword())
        .clientId("admin-cli")
        .build();
  }

  public void assignRole(String userId, Role roleName) {
    var realmResource = getKeycloakAdminClient().realm(keycloakConfig.getRealm());
    var role = realmResource.roles().get(roleName.name()).toRepresentation();
    realmResource.users().get(userId).roles().realmLevel().add(List.of(role));
  }
}