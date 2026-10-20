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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/vehiculo-conductor")
@Tag(name = "Vehículo-Conductor", description = "Relación entre conductores y los vehículos que pueden operar (Fase 2)")
public class VehiculoConductorController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("VehiculoConductorService")
    private IVehiculoConductorService vehiculoConductorService;

    @Operation(summary = "Asociar conductor a vehículo", description = "Asocia un conductor a un vehículo "
            + "que puede operar, con un estado inicial (PO, EA o RO).")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<VehiculoConductor> asociar(@RequestBody @Valid VehiculoConductorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoConductorService.asociar(request));
    }

    @Operation(summary = "Cambiar estado del conductor", description = "Cambia el estado del conductor en "
            + "su relación con un vehículo: PO (Puede Operar), EA (Espera de Aprobación) o RO (Restringido para Operar).")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/estado")
    public ResponseEntity<VehiculoConductor> cambiarEstado(@Parameter(description = "Id de la relación conductor-vehículo") @PathVariable("id") Long id,
            @RequestBody @Valid CambiarEstadoConductorRequest request) {
        return ResponseEntity.ok(vehiculoConductorService.cambiarEstado(id, request.getEstado()));
    }

    @Operation(summary = "Consultar relación por id")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoConductor> getById(@Parameter(description = "Id de la relación conductor-vehículo") @PathVariable("id") Long id) {
        return ResponseEntity.ok(vehiculoConductorService.findById(id));
    }

    @Operation(summary = "Listar relaciones conductor-vehículo", description = "Listado paginado de todas las asociaciones.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<VehiculoConductor>> listado(Pageable pageable) {
        return ResponseEntity.ok(vehiculoConductorService.consultar(pageable));
    }

    // ================ SERVICIO PUBLICO (no requiere token) ================
    @Operation(summary = "[Público] Conductores que pueden operar", description = "Lista las relaciones "
            + "conductor-vehículo cuyo estado es PO (Puede Operar). No requiere autenticación.")
    @GetMapping("/publico/pueden-operar")
    public ResponseEntity<List<VehiculoConductor>> getConductoresQuePuedenOperar() {
        return ResponseEntity.ok(vehiculoConductorService.findConductoresQuePuedenOperar());
    }
}
