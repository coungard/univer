package com.coungard.univer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Component
@Slf4j
public class StartupListener {

    @Autowired
    private Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void printHostAndPort() {
        String port = environment.getProperty("local.server.port");
        String address = "localhost"; // Обычно localhost, но можно получить и реальный IP, если нужно

        log.info("Application started at http://" + address + ":" + port);
    }
}