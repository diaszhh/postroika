package com.example.postroika.mapper;

import com.example.postroika.dto.OrdersDto;
import com.example.postroika.entity.Orders;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdersMapper {
    OrdersDto toDto(Orders e);
    Orders toEntity(OrdersDto dto);

    List<OrdersDto> toDtoList(List<Orders> entities);
    List<Orders> toEntityList(List<OrdersDto> dtos);
}
