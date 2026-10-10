package com.example.postroika.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Profile {
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal Jwt jwt) {
        // sub — это уникальный ID пользователя в Keycloak
        String keycloakId = jwt.getSubject();

        // Теперь можно сделать: clientRepository.findByKeycloakId(keycloakId)
        return ResponseEntity.ok("Ваш ID в Keycloak: " + keycloakId);
    }

}
