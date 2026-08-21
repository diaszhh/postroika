package com.example.postroika.mapper;

import com.example.postroika.dto.CustomerDto;
import com.example.postroika.entity.Customer;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer e);
    Customer toEntity(CustomerDto dto);

    List<CustomerDto> toDtoList(List<Customer> entities);
    List<Customer> toEntityList(List<CustomerDto> dtos);
}
