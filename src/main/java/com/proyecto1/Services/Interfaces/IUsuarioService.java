package com.proyecto1.Services.Interfaces;

import com.proyecto1.DTO.ApiKeyResponse;
import com.proyecto1.DTO.TokenResponse;

public interface IUsuarioService {

    void cambiarPassword(String login, String nuevaPassword);

    ApiKeyResponse regenerarApikey(String login);

    ApiKeyResponse login(String login, String password);

    TokenResponse authenticate(String login, String password);
}
