package com.proyecto1.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Body del servicio de cambio de password: la nueva contrasena.
public record CambioPasswordRequest(
        @NotBlank(message = "El password es obligatorio")
        @Size(min = 6, max = 50, message = "El password debe tener entre 6 y 50 caracteres")
        String password) {
}