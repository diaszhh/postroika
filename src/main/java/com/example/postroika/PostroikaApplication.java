package com.example.postroika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PostroikaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostroikaApplication.class, args);
    }

}
