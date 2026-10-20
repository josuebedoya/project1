package com.proyecto1.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1.DTO.ApiKeyResponse;
import com.proyecto1.DTO.CambiarPasswordRequest;
import com.proyecto1.DTO.LoginRequest;
import com.proyecto1.DTO.TokenResponse;
import com.proyecto1.Services.Interfaces.IUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/usuario")
@Tag(name = "Usuario", description = "Autenticación y gestión de credenciales de los usuarios administrativos (Fase 2)")
public class UsuarioController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("UsuarioService")
    private IUsuarioService usuarioService;

    @Operation(summary = "Autenticarse (obtener JWT)", description = "Valida login + password y devuelve "
            + "un JWT válido por 2 horas. Pega ese token con el botón Authorize de esta página para probar "
            + "el resto de los endpoints protegidos. No requiere token (es como se obtiene).")
    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(usuarioService.authenticate(request.getLogin(), request.getPassword()));
    }

    @Operation(summary = "Login básico (consultar apikey)", description = "Valida login + password y "
            + "devuelve el apikey del usuario. Se conserva como utilidad aparte, pero ya no es el mecanismo "
            + "de autenticación de la API (ver /authenticate). No requiere token.")
    @PostMapping("/login")
    public ResponseEntity<ApiKeyResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request.getLogin(), request.getPassword()));
    }

    @Operation(summary = "Cambiar password", description = "Cambia el password de un usuario específico; "
            + "el login va en la URL y la nueva contraseña en el body.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{login}/password")
    public ResponseEntity<Void> cambiarPassword(@Parameter(description = "Login del usuario") @PathVariable("login") String login,
            @RequestBody @Valid CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(login, request.getPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Regenerar apikey", description = "Genera un nuevo apikey para el usuario "
            + "indicado; el anterior deja de ser válido de inmediato.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{login}/apikey")
    public ResponseEntity<ApiKeyResponse> regenerarApikey(@Parameter(description = "Login del usuario") @PathVariable("login") String login) {
        return ResponseEntity.ok(usuarioService.regenerarApikey(login));
    }
}
