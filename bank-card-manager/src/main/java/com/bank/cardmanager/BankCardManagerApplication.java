package com.bank.cardmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
    com.bank.cardmanager.config.SecurityConfig.class,
    com.bank.cardmanager.config.OpenApiConfig.class,
    com.bank.cardmanager.config.WebConfig.class,
    com.bank.cardmanager.config.AsyncConfig.class,
    com.bank.cardmanager.config.CacheConfig.class,
    com.bank.cardmanager.config.ValidationConfig.class
})
public class BankCardManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankCardManagerApplication.class, args);
    }
}