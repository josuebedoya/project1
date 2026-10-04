package com.proyecto1.Services.Interfaces;

import com.proyecto1.DTO.ApiKeyResponse;

public interface IUsuarioService {

    void cambiarPassword(String login, String nuevaPassword);

    ApiKeyResponse regenerarApikey(String login);
}
