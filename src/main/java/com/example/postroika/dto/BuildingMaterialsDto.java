package com.example.postroika.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BuildingMaterialsDto {
    private Long id;

    private String name;

    private String description;

    private String unit;

    private BigDecimal price;

    private BigDecimal stockQuantity;

}
