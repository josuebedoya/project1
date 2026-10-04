package com.proyecto1.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1.DTO.ApiKeyResponse;
import com.proyecto1.DTO.CambiarPasswordRequest;
import com.proyecto1.Services.Interfaces.IUsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/usuario")
public class UsuarioController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("UsuarioService")
    private IUsuarioService usuarioService;

    // PUT: cambia el password de un usuario (login en la URL, password nuevo en el body).
    @PutMapping("/{login}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable("login") String login,
            @RequestBody @Valid CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(login, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    // GET: regenera el APIKey de un usuario especifico.
    @GetMapping("/{login}/apikey")
    public ResponseEntity<ApiKeyResponse> regenerarApikey(@PathVariable("login") String login) {
        return ResponseEntity.ok(usuarioService.regenerarApikey(login));
    }
}
