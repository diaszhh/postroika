package com.example.postroika.service;

import com.example.postroika.dto.OrdersDto;
import com.example.postroika.entity.Orders;
import com.example.postroika.mapper.OrdersMapper;
import com.example.postroika.repository.OrdersRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository repository;
    private final OrdersMapper mapper;

    public List<OrdersDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    public OrdersDto findById(Long id) {
        Orders entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapper.toDto(entity);
    }

    public OrdersDto create(OrdersDto dto) {
        Orders toSave = mapper.toEntity(dto);
        toSave.setId(null);
        Orders saved = repository.save(toSave);
        return mapper.toDto(saved);
    }

    public OrdersDto update(Long id, OrdersDto dto) {
        Orders existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existing.setOrderNumber(dto.getOrderNumber());
        existing.setOrderDate(dto.getOrderDate());
        existing.setStatus(dto.getStatus());
        existing.setTotalAmount(dto.getTotalAmount());
        Orders saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
