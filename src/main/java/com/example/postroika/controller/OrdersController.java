package com.example.postroika.controller;

import com.example.postroika.dto.OrdersDto;
import com.example.postroika.service.OrdersService;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService service;

    @GetMapping
    public List<OrdersDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public OrdersDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<OrdersDto> create(@RequestBody OrdersDto dto) {
        OrdersDto savedDto = service.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdersDto> update(@PathVariable Long id, @RequestBody OrdersDto dto) {
        OrdersDto upd = service.update(id, dto);
        return ResponseEntity.ok(upd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}