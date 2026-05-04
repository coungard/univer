package com.coungard.univer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${springdoc.api-docs.version}")
  private String version;

  @Value("${keycloak.auth-server-url}")
  private String keycloakAuthServerUrl;

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";
    final String issuerUri = keycloakAuthServerUrl + "/realms/univer-realm";

    return new OpenAPI()
        .info(new Info()
            .title("University API")
            .version(version)
            .description("REST API для управления университетом"))
        .components(new Components()
            .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("OAuth2 with Keycloak")
                .flows(new OAuthFlows()
                    .authorizationCode(new OAuthFlow()
                        .authorizationUrl(
                            keycloakAuthServerUrl + "/realms/univer-realm/protocol/openid-connect/auth?prompt=login")
                        .tokenUrl(keycloakAuthServerUrl + "/realms/univer-realm/protocol/openid-connect/token")
                        .scopes(new Scopes().addString("openid", "OpenID scope"))
                    )
                )
            )
        )
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName, "openid"));
  }
}