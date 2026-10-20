package com.proyecto1.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1.DTO.RutaConductorResponse;
import com.proyecto1.DTO.TrayectoRutaRequest;
import com.proyecto1.Entities.Trayecto;
import com.proyecto1.Services.Interfaces.ITrayectoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/trayecto")
@Tag(name = "Trayecto", description = "Rutas/paradas de un conductor con un vehículo específico y sus consultas (Fase 3)")
public class TrayectoController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("TrayectoService")
    private ITrayectoService trayectoService;

    @Operation(summary = "Crear ruta", description = "Crea una ruta completa (parada inicial, hasta 5 "
            + "intermedias y final) en una sola petición. Exige que el vehículo tenga todos sus documentos "
            + "HABILITADO y que el conductor pueda operar (PO) ese vehículo.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<List<Trayecto>> crearRuta(@RequestBody @Valid TrayectoRutaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trayectoService.crearRuta(request));
    }

    // ================ SERVICIOS PROTEGIDOS (requieren JWT) ================
    @Operation(summary = "Consultar ruta por código", description = "Devuelve, en orden, los trayectos "
            + "(paradas) de una ruta específica.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/consulta/ruta/{codigoRuta}")
    public ResponseEntity<List<Trayecto>> getByCodigoRuta(@Parameter(description = "Código de ruta") @PathVariable("codigoRuta") String codigoRuta) {
        return ResponseEntity.ok(trayectoService.findByCodigoRuta(codigoRuta));
    }

    @Operation(summary = "Buscar rutas por texto de ubicación", description = "Busca trayectos cuya "
            + "ubicación contiene el texto dado (ej. \"Ibagué\"), sin importar mayúsculas/minúsculas, para "
            + "cuando no se conoce el código de ruta exacto.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/consulta/ubicacion/{texto}")
    public ResponseEntity<List<Trayecto>> getByUbicacion(@Parameter(description = "Texto a buscar en la ubicación") @PathVariable("texto") String texto) {
        return ResponseEntity.ok(trayectoService.findByUbicacion(texto));
    }

    @Operation(summary = "Códigos de ruta de un conductor", description = "Devuelve, agrupados, los "
            + "códigos de ruta en los que ha participado un conductor, por su número de identificación.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/consulta/conductor/{identificacion}")
    public ResponseEntity<List<String>> getCodigosRutaByConductor(
            @Parameter(description = "Número de identificación del conductor") @PathVariable("identificacion") String identificacion) {
        return ResponseEntity.ok(trayectoService.findCodigosRutaByConductor(identificacion));
    }

    @Operation(summary = "Rutas por placa de vehículo", description = "Devuelve, agrupados, el código de "
            + "ruta y el conductor asociado al trayecto, para un vehículo específico.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/consulta/vehiculo/{placa}")
    public ResponseEntity<List<RutaConductorResponse>> getRutasByPlaca(@Parameter(description = "Placa del vehículo") @PathVariable("placa") String placa) {
        return ResponseEntity.ok(trayectoService.findRutasByPlaca(placa));
    }

    @Operation(summary = "Rutas restringidas", description = "Lista los trayectos cuyo vehículo NO está "
            + "habilitado (algún documento no HABILITADO) o cuyo conductor está Restringido para Operar (RO).")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/consulta/restringidos")
    public ResponseEntity<List<Trayecto>> getRestringidos() {
        return ResponseEntity.ok(trayectoService.findRestringidos());
    }
}
