package com.example.bankcards.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .components(createComponents())
                .addSecurityItem(createSecurityRequirement());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Bank Cards API")
                .description("REST API для управления банковскими картами")
                .version("0.0.1-SNAPSHOT")
                .contact(createContact());
    }

    private Contact createContact() {
        return new Contact()
                .name("noname-jack")
                .email("aleksander.moskovchenko@mail.ru");
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createBearerAuthScheme());
    }

    private SecurityScheme createBearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT токен авторизации");
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}