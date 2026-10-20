package com.proyecto1.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1.DTO.PersonaCreadaResponse;
import com.proyecto1.DTO.PersonaPorTipoResponse;
import com.proyecto1.Entities.Persona;
import com.proyecto1.Services.Interfaces.IPersonaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/persona")
@Tag(name = "Persona", description = "Conductores y administrativos; crea un Usuario automáticamente para los administrativos (Fase 2)")
public class PersonaController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("PersonaService")
    private IPersonaService personaService;

    @Operation(summary = "Crear persona", description = "Crea un conductor o un administrativo. Si es "
            + "ADMINISTRATIVO, genera automáticamente su Usuario (login, password y apikey). No requiere "
            + "token: es el único endpoint de escritura abierto, necesario para poder crear el primer "
            + "administrador del sistema.")
    @PostMapping
    public ResponseEntity<PersonaCreadaResponse> agregarPersona(@RequestBody @Valid Persona persona) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.crear(persona));
    }

    @Operation(summary = "Editar persona", description = "Actualiza los datos de una persona existente "
            + "(el id va dentro del body). No permite cambiar el tipo de persona.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    public ResponseEntity<Persona> editarPersona(@RequestBody @Valid Persona persona) {
        return ResponseEntity.ok(personaService.actualizar(persona));
    }

    @Operation(summary = "Consultar persona por id")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<Persona> getById(@Parameter(description = "Id de la persona") @PathVariable("id") Long id) {
        return ResponseEntity.ok(personaService.findById(id));
    }

    @Operation(summary = "Listar personas", description = "Listado paginado de todas las personas registradas.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<Persona>> listadoPersonas(Pageable pageable) {
        return ResponseEntity.ok(personaService.consultarPersonas(pageable));
    }

    // ================ SERVICIO PUBLICO (no requiere token) ================
    @Operation(summary = "[Público] Total de personas por tipo", description = "Devuelve el total de "
            + "personas agrupadas por tipo (Conductor / Administrativo). No requiere autenticación.")
    @GetMapping("/publico/conteo-por-tipo")
    public ResponseEntity<List<PersonaPorTipoResponse>> getConteoPorTipo() {
        return ResponseEntity.ok(personaService.contarPorTipo());
    }
}