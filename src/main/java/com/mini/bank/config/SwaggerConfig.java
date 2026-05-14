package com.mini.bank.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
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
    public OpenAPI miniBankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini Digital Banking API")
                        .description("""
                                Secure Digital Banking Backend System
                                
                                Features:
                                - JWT Authentication
                                - Account Management
                                - Deposits & Withdrawals
                                - Money Transfers
                                - Ledger History
                                - Role Based Access
                                - Audit Logging
                                - Pessimistic Locking
                                """)
                        .contact(new Contact()
                                .name("Shehryar")
                                .email("shehryarmoazzam19@gmail.com")
                                .url("https://github.com/Shehryar2000")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project Repository")
                        .url("https://github.com/Shehryar2000/mini-digital-bank"))
                .addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
