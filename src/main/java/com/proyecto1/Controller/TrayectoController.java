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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/trayecto")
public class TrayectoController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("TrayectoService")
    private ITrayectoService trayectoService;

    // POST: crea una ruta completa (parada inicial, intermedias y final) en una sola petición.
    @PostMapping
    public ResponseEntity<List<Trayecto>> crearRuta(@RequestBody @Valid TrayectoRutaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trayectoService.crearRuta(request));
    }

    // ================ SERVICIOS PROTEGIDOS (requieren header X-API-KEY) ================
    // Trayectos de una ruta, en orden.
    @GetMapping("/consulta/ruta/{codigoRuta}")
    public ResponseEntity<List<Trayecto>> getByCodigoRuta(@PathVariable("codigoRuta") String codigoRuta) {
        return ResponseEntity.ok(trayectoService.findByCodigoRuta(codigoRuta));
    }

    // Codigos de ruta (agrupados) de un conductor, por su número de identificación.
    @GetMapping("/consulta/conductor/{identificacion}")
    public ResponseEntity<List<String>> getCodigosRutaByConductor(
            @PathVariable("identificacion") String identificacion) {
        return ResponseEntity.ok(trayectoService.findCodigosRutaByConductor(identificacion));
    }

    // Codigo de ruta y conductor asociado (agrupados), por placa de vehiculo.
    @GetMapping("/consulta/vehiculo/{placa}")
    public ResponseEntity<List<RutaConductorResponse>> getRutasByPlaca(@PathVariable("placa") String placa) {
        return ResponseEntity.ok(trayectoService.findRutasByPlaca(placa));
    }

    // Rutas/trayectos donde el vehiculo no esta habilitado o el conductor esta restringido.
    @GetMapping("/consulta/restringidos")
    public ResponseEntity<List<Trayecto>> getRestringidos() {
        return ResponseEntity.ok(trayectoService.findRestringidos());
    }
}
