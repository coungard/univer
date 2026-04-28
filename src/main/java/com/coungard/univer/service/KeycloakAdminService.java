package com.coungard.univer.service;

import com.coungard.univer.config.KeycloakConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakAdminService {

    private final KeycloakConfig keycloakConfig;

    public KeycloakAdminService(KeycloakConfig keycloakConfig) {
        this.keycloakConfig = keycloakConfig;
    }

    public String createUser(String firstName, String lastName, String email, String password) {
        Keycloak keycloak = getKeycloakAdminClient();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        var response = keycloak.realm(keycloakConfig.getRealm()).users().create(user);
        String locationHeader = response.getLocation().toString();
        return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

    public void assignStudentRole(String userId) {
        var realmResource = getKeycloakAdminClient().realm(keycloakConfig.getRealm());
        var role = realmResource.roles().get("STUDENT").toRepresentation();
        realmResource.users().get(userId).roles().realmLevel().add(List.of(role));
    }

    private Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakConfig.getAuthServerUrl())
                .realm(keycloakConfig.getRealm())
                .username(keycloakConfig.getAdminUsername())
                .password(keycloakConfig.getAdminPassword())
                .clientId(keycloakConfig.getAdminClientId())
                .build();
    }
}