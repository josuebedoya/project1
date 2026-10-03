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
import com.proyecto1.Entities.Persona;
import com.proyecto1.Services.Interfaces.IPersonaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/persona")
public class PersonaController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("PersonaService")
    private IPersonaService personaService;

    // POST: crea la persona; si es ADMINISTRATIVO devuelve tambien el usuario generado.
    @PostMapping
    public ResponseEntity<PersonaCreadaResponse> agregarPersona(@RequestBody @Valid Persona persona) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.crear(persona));
    }

    // PUT: actualiza los datos de la persona (el id va en el body).
    @PutMapping
    public ResponseEntity<Persona> editarPersona(@RequestBody @Valid Persona persona) {
        return ResponseEntity.ok(personaService.actualizar(persona));
    }

    // GET por id
    @GetMapping("/{id}")
    public ResponseEntity<Persona> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(personaService.findById(id));
    }

    // GET listado paginado
    @GetMapping
    public ResponseEntity<List<Persona>> listadoPersonas(Pageable pageable) {
        return ResponseEntity.ok(personaService.consultarPersonas(pageable));
    }
}