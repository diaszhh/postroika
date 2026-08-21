package com.example.postroika.mapper;

import com.example.postroika.dto.BuildingMaterialsDto;
import com.example.postroika.entity.BuildingMaterials;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BuildingMaterialsMapper {
    BuildingMaterialsDto toDto(BuildingMaterials e);
    BuildingMaterials toEntity(BuildingMaterialsDto dto);

    List<BuildingMaterialsDto> toDtoList(List<BuildingMaterials> entities);
    List<BuildingMaterials> toEntityList(List<BuildingMaterialsDto> dtos);
    
}
