package com.example.postroika.mapper;

import com.example.postroika.dto.ClientDto;
import com.example.postroika.entity.Client;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDto toDto(Client e);
    Client toEntity(ClientDto dto);

    List<ClientDto> toDtoList(List<Client> entities);
    List<Client> toEntityList(List<ClientDto> dtos);
}
