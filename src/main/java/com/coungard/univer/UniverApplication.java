package com.coungard.univer;

import com.coungard.univer.config.KeycloakConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

//@EnableConfigurationProperties(KeycloakConfig.class)
@SpringBootApplication
@Slf4j
public class UniverApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniverApplication.class, args);
    }

//    private final WebServerApplicationContext webServerAppCtxt;
//
//    @Autowired
//    public UniverApplication(WebServerApplicationContext webServerAppCtxt) {
//        this.webServerAppCtxt = webServerAppCtxt;
//    }
//
//    @PostConstruct
//    public void logServerInfo() {
//        int port = webServerAppCtxt.getWebServer().getPort();
//        String host = "http://localhost:" + port;
//        String swaggerUrl = host + "/swagger-ui.html";
//        String swaggerDocs = host + "/v3/api-docs";
//        log.info("Swagger UI: " + swaggerUrl);
//        log.info("OpenAPI JSON: " + swaggerDocs);
//    }
}
