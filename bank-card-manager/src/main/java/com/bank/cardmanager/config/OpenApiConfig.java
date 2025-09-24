package com.bank.cardmanager.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Bank Card Manager API")
                .description("Spring Boot application for managing bank cards with JWT authentication")
                .version("1.0.0")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@bank.com")
                    .url("https://bank.com/support"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://springdoc.org")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080" + contextPath)
                    .description("Development Server"),
                new Server()
                    .url("https://api.bank.com" + contextPath)
                    .description("Production Server")
            ))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}