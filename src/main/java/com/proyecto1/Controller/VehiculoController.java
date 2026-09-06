package com.proyecto1.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1.DTO.DocumentoAsociadoRequest;
import com.proyecto1.DTO.VehiculoDetalleResponse;
import com.proyecto1.DTO.VehiculoRequest;
import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.TipoVehiculo;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;
import com.proyecto1.Services.Interfaces.IVehiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/vehiculo")
public class VehiculoController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("VehiculoService")
    private IVehiculoService vehiculoService;

    // ==========METODOS HTTP CRUD==================
    // POST: crea un vehiculo, requiere al menos un documento asociado en el body.
    @PostMapping
    public ResponseEntity<VehiculoDetalleResponse> agregarVehiculo(@RequestBody @Valid VehiculoRequest request) {
        Vehiculo creado = vehiculoService.crear(request);
        List<VehiculoDocumento> documentos = vehiculoService.findDocumentosByVehiculoId(creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new VehiculoDetalleResponse(creado, documentos));
    }

    // PUT: actualiza los datos propios del vehiculo (no sus documentos).
    @PutMapping
    public ResponseEntity<Vehiculo> editarVehiculo(@RequestBody @Valid Vehiculo vehiculo) {
        return ResponseEntity.ok(vehiculoService.actualizar(vehiculo));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable("id") Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET por id (incluye documentos asociados)
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDetalleResponse> getById(@PathVariable("id") Long id) {
        Vehiculo vehiculo = vehiculoService.findById(id);
        List<VehiculoDocumento> documentos = vehiculoService.findDocumentosByVehiculoId(id);
        return ResponseEntity.ok(new VehiculoDetalleResponse(vehiculo, documentos));
    }

    // GET listado paginado
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listadoVehiculos(Pageable pageable) {
        return ResponseEntity.ok(vehiculoService.consultarVehiculos(pageable));
    }

    // ================MÉTODOS HTTP DE BÚSQUEDA ================
    // Buscar vehiculo por numero de placa.
    @GetMapping("/placa/{placa}")
    public ResponseEntity<Vehiculo> getByPlaca(@PathVariable("placa") String placa) {
        return ResponseEntity.ok(vehiculoService.findByPlaca(placa));
    }

    // Buscar vehiculos por tipo de vehiculo (AUTOMOVIL | MOTOCICLETA).
    @GetMapping("/tipo/{tipoVehiculo}")
    public ResponseEntity<List<Vehiculo>> getByTipoVehiculo(@PathVariable("tipoVehiculo") TipoVehiculo tipoVehiculo) {
        return ResponseEntity.ok(vehiculoService.findByTipoVehiculo(tipoVehiculo));
    }

    // Buscar vehiculos que tengan en comun un tipo de documento (por id del documento parametrizado).
    @GetMapping("/documento/{documentoId}")
    public ResponseEntity<List<Vehiculo>> getByDocumento(@PathVariable("documentoId") Long documentoId) {
        return ResponseEntity.ok(vehiculoService.findByDocumentoId(documentoId));
    }

    // Buscar vehiculos segun el estado del documento asociado (HABILITADO | VENCIDO | EN_VERIFICACION).
    @GetMapping("/estado-documento/{estado}")
    public ResponseEntity<List<Vehiculo>> getByEstadoDocumento(@PathVariable("estado") EstadoDocumento estado) {
        return ResponseEntity.ok(vehiculoService.findByEstadoDocumento(estado));
    }

    // ================ AGREGAR DOCUMENTOS A UN VEHICULO EXISTENTE ================
    @PostMapping("/{id}/documentos")
    public ResponseEntity<List<VehiculoDocumento>> agregarDocumentos(@PathVariable("id") Long id,
            @RequestBody @Valid List<DocumentoAsociadoRequest> documentos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.agregarDocumentos(id, documentos));
    }
}
