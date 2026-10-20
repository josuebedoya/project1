package com.proyecto1.Config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

// Metadata general de Swagger/OpenAPI y el esquema de seguridad Bearer (JWT) usado por el
// boton "Authorize" de la UI: pega ahi el token que devuelve POST /proyecto1/usuario/authenticate
// y automaticamente se agrega el header Authorization a todas las pruebas que hagas desde Swagger.
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Proyecto1 - Gestión de Trayectos",
        version = "v1",
        description = "API REST del proyecto final (Fases 1-4): vehículos, documentos, personas, "
                + "usuarios, relación conductor-vehículo, trayectos y cargue masivo por Excel. "
                + "La mayoría de los endpoints requieren autenticación: obtén un token en "
                + "POST /proyecto1/usuario/authenticate y pégalo con el botón Authorize de esta página."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token obtenido en POST /proyecto1/usuario/authenticate")
public class OpenApiConfig {
}
