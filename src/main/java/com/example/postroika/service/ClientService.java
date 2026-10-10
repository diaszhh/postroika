package com.example.postroika.service;

import com.example.postroika.dto.ClientDto;
import com.example.postroika.entity.Client;
import com.example.postroika.mapper.ClientMapper;
import com.example.postroika.repository.ClientRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    public List<ClientDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public ClientDto findById(Long id) {
        Client entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDto(entity);
    }

    public ClientDto create(ClientDto dto) {
        Client toSave = mapper.toEntity(dto);
        toSave.setId(null);
        Client saved = repository.save(toSave);
        return mapper.toDto(saved);
    }

    public ClientDto update(Long id, ClientDto dto) {
        Client existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        Client saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    // Поиск профиля по Keycloak ID
    public ClientDto findByKeycloakId(String keycloakId) {
        Client client = repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDto(client);
    }

    // Создание записи в локальной БД при первой авторизации
    public ClientDto createWithKeycloak(ClientDto dto, String keycloakId) {
        // Проверяем, возможно этот пользователь уже создавал запись у нас в БД
        if (repository.findByKeycloakId(keycloakId).isPresent()) {
            throw new IllegalStateException("Локальный профиль для этого пользователя уже существует");
        }

        Client client = mapper.toEntity(dto);
        client.setKeycloakId(keycloakId); // Принудительно сетим ID из токена Keycloak

        Client saved = repository.save(client);
        return mapper.toDto(saved);
    }

}
