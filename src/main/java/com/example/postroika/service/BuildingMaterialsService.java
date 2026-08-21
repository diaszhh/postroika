package com.example.postroika.service;

import com.example.postroika.dto.BuildingMaterialsDto;

import java.util.ArrayList;
import java.util.List;

import com.example.postroika.entity.BuildingMaterials;
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
}
