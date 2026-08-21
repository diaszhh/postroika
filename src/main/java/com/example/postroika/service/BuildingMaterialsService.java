package com.example.postroika.service;

import com.example.postroika.dto.BuildingMaterialsDto;

import java.util.List;

import com.example.postroika.dto.MaterialAvailabilityDto;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.postroika.mapper.BuildingMaterialsMapper;
import com.example.postroika.repository.BuildingMaterialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildingMaterialsService {

    private final BuildingMaterialsRepository buildingMaterialsRepository;

    private final BuildingMaterialsMapper buildingMaterialsMapper;

    @Cacheable(value = "buildingMaterials")
    public List<BuildingMaterialsDto> findAll() {
        return buildingMaterialsMapper.toDtoList(buildingMaterialsRepository.findAll());
    }

    @Cacheable(value = "buildingMaterials", key = "#id")
    public BuildingMaterialsDto findById(Long id) {
        return buildingMaterialsMapper.toDto(buildingMaterialsRepository.findById(id).orElseThrow());
    }

    @CacheEvict(value = "buildingMaterials", allEntries = true)

    public BuildingMaterialsDto create(BuildingMaterialsDto dto) {
        return buildingMaterialsMapper.toDto(buildingMaterialsRepository.save(buildingMaterialsMapper.toEntity(dto)));
    }

    @CacheEvict(value = "buildingMaterials", allEntries = true)
    public BuildingMaterialsDto update(Long id, BuildingMaterialsDto dto) {
        BuildingMaterialsDto dtoUpd = findById(id);
        dtoUpd.setId(id);
        dtoUpd.setName(dto.getName());
        dtoUpd.setUnit(dto.getUnit());
        dtoUpd.setPrice(dto.getPrice());
        dtoUpd.setDescription(dto.getDescription());
        buildingMaterialsRepository.save(buildingMaterialsMapper.toEntity(dtoUpd));
        return dtoUpd;

    }

    @CacheEvict(value = "buildingMaterials", allEntries = true)
    public void delete(Long id) {
        buildingMaterialsRepository.deleteById(id);
    }

    public MaterialAvailabilityDto checkAvailability(Long materialId, BigDecimal requestedQuantity) {
        if (materialId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID материала не должен быть пустым");
        }

        if (requestedQuantity == null || requestedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Запрашиваемое количество должно быть больше 0");
        }

        return buildingMaterialsRepository
                .findById(materialId)
                .map(material -> {
                    BigDecimal stockQuantity = material.getStockQuantity();

                    if (stockQuantity == null) {
                        stockQuantity = BigDecimal.ZERO;
                    }

                    boolean available = stockQuantity.compareTo(requestedQuantity) >= 0;

                    return MaterialAvailabilityDto.builder()
                            .materialId(material.getId())
                            .name(material.getName())
                            .unit(material.getUnit())
                            .requestedQuantity(requestedQuantity)
                            .availableQuantity(stockQuantity)
                            .available(available)
                            .message(available ? "Материал доступен" : "Недостаточно материала на складе")
                            .build();
                })
                .orElseGet(() -> MaterialAvailabilityDto.builder()
                        .materialId(materialId)
                        .requestedQuantity(requestedQuantity)
                        .availableQuantity(BigDecimal.ZERO)
                        .available(false)
                        .message("Материал не найден")
                        .build());
    }
}
