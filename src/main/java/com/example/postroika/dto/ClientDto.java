package com.example.postroika.dto;

import lombok.Data;

@Data
public class ClientDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;
}
