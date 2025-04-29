package com.getir.library_management.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Defines a security scheme named "bearerAuth" for Swagger UI
@SecurityScheme(
        name = "bearerAuth", // This name is used in @SecurityRequirement
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class OpenApiConfig {
}