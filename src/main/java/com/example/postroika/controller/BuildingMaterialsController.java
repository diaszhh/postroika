package com.example.postroika.controller;

import com.example.postroika.dto.BuildingMaterialsDto;

import java.net.URI;
import java.util.List;

import com.example.postroika.service.BuildingMaterialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/building-materials")
@RequiredArgsConstructor
public class BuildingMaterialsController {

    private final BuildingMaterialsService buildingMaterialsService;

    @GetMapping
    public List<BuildingMaterialsDto> findAll() {
        return buildingMaterialsService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingMaterialsDto> findById(@PathVariable Long id) {
        BuildingMaterialsDto buildingMaterialsDto = buildingMaterialsService.findById(id);
        return ResponseEntity.ok(buildingMaterialsDto);
    }

    @PostMapping
    public ResponseEntity<BuildingMaterialsDto> create(@RequestBody BuildingMaterialsDto dto) {
        BuildingMaterialsDto savedDto = buildingMaterialsService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BuildingMaterialsDto> update(@PathVariable Long id, @RequestBody BuildingMaterialsDto dto) {
        BuildingMaterialsDto updDto = buildingMaterialsService.update(id, dto);
        return ResponseEntity.ok(updDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        buildingMaterialsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
