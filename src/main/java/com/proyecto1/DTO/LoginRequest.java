package com.proyecto1.DTO;

import jakarta.validation.constraints.NotBlank;

// Body para iniciar sesion: a cambio de login+password devuelve el apikey del usuario.
public class LoginRequest {

    @NotBlank(message = "El login es obligatorio")
    private String login;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    public LoginRequest() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
