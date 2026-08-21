package com.example.postroika.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrdersDto {
    private Long id;

    private String orderNumber;

    private LocalDateTime orderDate;

    private String status;

    private BigDecimal totalAmount;
}
