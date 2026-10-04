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

import com.proyecto1.DTO.CambiarEstadoConductorRequest;
import com.proyecto1.DTO.VehiculoConductorRequest;
import com.proyecto1.Entities.VehiculoConductor;
import com.proyecto1.Services.Interfaces.IVehiculoConductorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/vehiculo-conductor")
public class VehiculoConductorController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("VehiculoConductorService")
    private IVehiculoConductorService vehiculoConductorService;

    // POST: asocia un conductor a los vehiculos que puede operar.
    @PostMapping
    public ResponseEntity<VehiculoConductor> asociar(@RequestBody @Valid VehiculoConductorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoConductorService.asociar(request));
    }

    // PUT: cambia el estado del conductor en relacion con el vehiculo (PO, EA, RO).
    @PutMapping("/{id}/estado")
    public ResponseEntity<VehiculoConductor> cambiarEstado(@PathVariable("id") Long id,
            @RequestBody @Valid CambiarEstadoConductorRequest request) {
        return ResponseEntity.ok(vehiculoConductorService.cambiarEstado(id, request.getEstado()));
    }

    // GET por id
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoConductor> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(vehiculoConductorService.findById(id));
    }

    // GET listado paginado
    @GetMapping
    public ResponseEntity<List<VehiculoConductor>> listado(Pageable pageable) {
        return ResponseEntity.ok(vehiculoConductorService.consultar(pageable));
    }

    // ================ SERVICIO PUBLICO (no requiere token) ================
    // Conductores que pueden operar.
    @GetMapping("/publico/pueden-operar")
    public ResponseEntity<List<VehiculoConductor>> getConductoresQuePuedenOperar() {
        return ResponseEntity.ok(vehiculoConductorService.findConductoresQuePuedenOperar());
    }
}
