package com.coungard.univer.config;

import com.coungard.univer.security.KeycloakRoleConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources",
                "/swagger-resources/**",
                "/webjars/**",
                "/api/public"
            ).permitAll()
            // 🔥 Добавляем публичный доступ к регистрации
            .requestMatchers("/api/v1/students/register").permitAll()
            .requestMatchers("/api/v1/teachers/register").permitAll()
            // Экран регистрации ещё не имеет токена, но должен дать выбрать университет
            // (RegisterStudentRequest.universityId) / кафедру (RegisterTeacherRequest.departmentId)
            // — без этого регистрация в принципе невозможна: курица и яйцо (нужен токен, чтобы
            // получить список для формы, которая этот токен и выдаёт).
            .requestMatchers(HttpMethod.GET,
                "/api/v1/universities", "/api/v1/universities/**",
                "/api/v1/faculties/**",
                "/api/v1/departments/**"
            ).permitAll()
            // Отключено намеренно: удобно для локальной отладки — требуем JWT
            // даже на списковых эндпоинтах, чтобы не путать публичное и приватное поведение.
//            .requestMatchers("/api/v1/teachers*").permitAll()
//            .requestMatchers("/api/v1/programs*").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
            )
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> {
        });

    return http.build();
  }

  /**
   * Настройка JwtDecoder для проверки подписи JWT от Keycloak.
   * <p>
   * Собран через {@code withJwkSetUri(...)}, а не {@code withIssuerLocation(...)}/
   * {@code JwtValidators.createDefaultWithIssuer(...)}, поэтому claim {@code iss} НЕ проверяется —
   * валидируются только подпись и {@code exp}/{@code nbf}. Это осознанно нужно для мобильного флоу
   * (Authorization Code + PKCE, см. MOBILE.md, issue #54): в dev локальный Keycloak слушает и HTTP
   * ({@code keycloak.auth-server-url}, 8082 — password grant, back-channel), и HTTPS (8443 — только
   * браузерная страница логина, cookie сессии требует настоящего TLS), и токены с этих двух адресов
   * несут разный {@code iss}, хотя подписаны одним и тем же ключом одного и того же realm'а. Если
   * понадобится включить проверку issuer — нужно одновременно разрешить оба адреса как валидные, иначе
   * токены мобильного клиента начнут получать 401.
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(issuerUri + "/protocol/openid-connect/certs").build();
  }

  /**
   * Конвертер JWT с извлечением ролей из токена Keycloak
   */
  private JwtAuthenticationConverter grantedAuthoritiesExtractor() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        new KeycloakRoleConverter()
    );
    return jwtAuthenticationConverter;
  }
}