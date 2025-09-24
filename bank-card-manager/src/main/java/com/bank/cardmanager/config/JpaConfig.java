package com.bank.cardmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.bank.cardmanager.repository")
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration will be handled by Spring Boot auto-configuration
    // Custom settings can be added here if needed
}