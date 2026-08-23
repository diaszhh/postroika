package com.example.postroika.controller;

import com.example.postroika.dto.ClientDto;
import com.example.postroika.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // Получить профиль ИМЕННО текущего пользователя по его токену Keycloak
    @GetMapping("/my-profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientDto> findMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject(); // Вытаскиваем "sub" из токена
        ClientDto dto = clientService.findByKeycloakId(keycloakId);
        return ResponseEntity.ok(dto);
    }

    // Посмотреть всех клиентов может, например, только МЕНЕДЖЕР или АДМИН
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClientDto> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')") // Нужна проверка в сервисе, его ли это ID
    public ClientDto findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    // При создании клиента из фронтенда/Postman мы передаем токен,
    // и бэкенд сам забирает оттуда keycloakId
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientDto> create(@RequestBody ClientDto dto, @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        ClientDto savedDto = clientService.createWithKeycloak(dto, keycloakId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientDto dto, @AuthenticationPrincipal Jwt jwt) {
        // Получаем текущего клиента из базы по его токену
        ClientDto currentClient = clientService.findByKeycloakId(jwt.getSubject());

        // Проверяем, совпадает ли ID из пути с ID залогиненного пользователя
        if (!currentClient.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы можете редактировать только свой профиль!");
        }

        ClientDto upd = clientService.update(id, dto);
        return ResponseEntity.ok(upd);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
