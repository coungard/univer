package com.coungard.univer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@Slf4j
public class UniverApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniverApplication.class, args);
    }
}
