package com.example.postroika.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialAvailabilityDto {

    private Long materialId;

    private String name;

    private String unit;

    private BigDecimal requestedQuantity;

    private BigDecimal availableQuantity;

    private boolean available;

    private String message;
}