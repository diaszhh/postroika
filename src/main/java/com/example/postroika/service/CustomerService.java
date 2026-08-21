package com.example.postroika.service;

import com.example.postroika.dto.CustomerDto;
import com.example.postroika.entity.Customer;
import com.example.postroika.mapper.CustomerMapper;
import com.example.postroika.repository.CustomerRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public List<CustomerDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public CustomerDto findById(Long id) {
        Customer entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDto(entity);
    }

    public CustomerDto create(CustomerDto dto) {
        Customer toSave = mapper.toEntity(dto);
        toSave.setId(null);
        Customer saved = repository.save(toSave);
        return mapper.toDto(saved);
    }

    public CustomerDto update(Long id, CustomerDto dto) {
        Customer existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        Customer saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
