package com.coungard.univer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureController {

    @GetMapping("/api/public")
    public String publicEndpoint() {
        return "Hello from public API!";
    }

    @GetMapping("/api/secure")
    public String secureEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return "Hello, " + jwt.getSubject() + "! This is a secure endpoint.";
    }
}