package com.proyecto1.DTO;

import jakarta.validation.constraints.NotBlank;

// Body para cambiar el password de un usuario (el login va en la URL).
public class CambiarPasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String password;

    public CambiarPasswordRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
