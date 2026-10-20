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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto1.DTO.DocumentoActualizadoRequest;
import com.proyecto1.DTO.DocumentoAsociadoRequest;
import com.proyecto1.DTO.VehiculoCompletoResponse;
import com.proyecto1.DTO.VehiculoDetalleResponse;
import com.proyecto1.DTO.VehiculoRequest;
import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.TipoVehiculo;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;
import com.proyecto1.Services.Interfaces.IVehiculoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/vehiculo")
@Tag(name = "Vehículo", description = "CRUD de vehículos, sus documentos asociados y búsquedas (Fase 1-2)")
public class VehiculoController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("VehiculoService")
    private IVehiculoService vehiculoService;

    // ==========METODOS HTTP CRUD==================
    @Operation(summary = "Crear vehículo", description = "Crea un vehículo junto con al menos un documento "
            + "asociado (obligatorio). Cada documento nace en estado EN_VERIFICACION.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<VehiculoDetalleResponse> agregarVehiculo(@RequestBody @Valid VehiculoRequest request) {
        Vehiculo creado = vehiculoService.crear(request);
        List<VehiculoDocumento> documentos = vehiculoService.findDocumentosByVehiculoId(creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new VehiculoDetalleResponse(creado, documentos));
    }

    @Operation(summary = "Editar vehículo", description = "Actualiza los datos propios del vehículo "
            + "(no modifica sus documentos asociados). El id va dentro del body.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    public ResponseEntity<Vehiculo> editarVehiculo(@RequestBody @Valid Vehiculo vehiculo) {
        return ResponseEntity.ok(vehiculoService.actualizar(vehiculo));
    }

    @Operation(summary = "Eliminar vehículo", description = "Elimina el vehículo y sus relaciones con "
            + "documentos y conductores.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@Parameter(description = "Id del vehículo") @PathVariable("id") Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar vehículo por id", description = "Devuelve el vehículo junto con los "
            + "documentos que tiene asociados.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDetalleResponse> getById(@Parameter(description = "Id del vehículo") @PathVariable("id") Long id) {
        Vehiculo vehiculo = vehiculoService.findById(id);
        List<VehiculoDocumento> documentos = vehiculoService.findDocumentosByVehiculoId(id);
        return ResponseEntity.ok(new VehiculoDetalleResponse(vehiculo, documentos));
    }

    @Operation(summary = "Listar vehículos", description = "Listado paginado de todos los vehículos registrados.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<Vehiculo>> listadoVehiculos(Pageable pageable) {
        return ResponseEntity.ok(vehiculoService.consultarVehiculos(pageable));
    }

    // ================MÉTODOS HTTP DE BÚSQUEDA ================
    @Operation(summary = "Buscar por placa", description = "Busca un vehículo por su número de placa exacto.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/placa/{placa}")
    public ResponseEntity<Vehiculo> getByPlaca(@Parameter(description = "Placa del vehículo") @PathVariable("placa") String placa) {
        return ResponseEntity.ok(vehiculoService.findByPlaca(placa));
    }

    @Operation(summary = "Buscar por tipo de vehículo", description = "Lista los vehículos de un tipo "
            + "específico (AUTOMOVIL o MOTOCICLETA).")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/tipo/{tipoVehiculo}")
    public ResponseEntity<List<Vehiculo>> getByTipoVehiculo(@PathVariable("tipoVehiculo") TipoVehiculo tipoVehiculo) {
        return ResponseEntity.ok(vehiculoService.findByTipoVehiculo(tipoVehiculo));
    }

    @Operation(summary = "Buscar por documento en común", description = "Lista los vehículos que tienen "
            + "asociado un mismo documento parametrizado, dado su id.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/documento/{documentoId}")
    public ResponseEntity<List<Vehiculo>> getByDocumento(@Parameter(description = "Id del documento parametrizado") @PathVariable("documentoId") Long documentoId) {
        return ResponseEntity.ok(vehiculoService.findByDocumentoId(documentoId));
    }

    @Operation(summary = "Buscar por estado de documento", description = "Lista los vehículos que tienen "
            + "algún documento en el estado indicado (HABILITADO, VENCIDO o EN_VERIFICACION).")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/estado-documento/{estado}")
    public ResponseEntity<List<Vehiculo>> getByEstadoDocumento(@PathVariable("estado") EstadoDocumento estado) {
        return ResponseEntity.ok(vehiculoService.findByEstadoDocumento(estado));
    }

    // ================ AGREGAR/ACTUALIZAR DOCUMENTOS DE UN VEHICULO EXISTENTE ================
    @Operation(summary = "Agregar documentos a un vehículo", description = "Asocia uno o varios documentos "
            + "nuevos a un vehículo existente (opcionalmente con el PDF en Base64). Nacen en EN_VERIFICACION.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/documentos")
    public ResponseEntity<List<VehiculoDocumento>> agregarDocumentos(@Parameter(description = "Id del vehículo") @PathVariable("id") Long id,
            @RequestBody @Valid List<DocumentoAsociadoRequest> documentos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.agregarDocumentos(id, documentos));
    }

    @Operation(summary = "Actualizar documentos de un vehículo", description = "Actualiza uno o varios "
            + "documentos ya asociados a un vehículo (fechas, estado y, opcionalmente, el PDF en Base64).")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/documentos")
    public ResponseEntity<List<VehiculoDocumento>> actualizarDocumentos(@Parameter(description = "Id del vehículo") @PathVariable("id") Long id,
            @RequestBody @Valid List<DocumentoActualizadoRequest> documentos) {
        return ResponseEntity.ok(vehiculoService.actualizarDocumentos(id, documentos));
    }

    // ================ SERVICIOS PUBLICOS (no requieren token) ================
    @Operation(summary = "[Público] Vehículos con documentos vencidos", description = "Lista todos los "
            + "vehículos que tienen al menos un documento en estado VENCIDO. No requiere autenticación.")
    @GetMapping("/publico/documentos-vencidos")
    public ResponseEntity<List<Vehiculo>> getVehiculosConDocumentosVencidos() {
        return ResponseEntity.ok(vehiculoService.findByEstadoDocumento(EstadoDocumento.VENCIDO));
    }

    @Operation(summary = "[Público] Vehículos con documentos por vencer", description = "Lista los vehículos "
            + "con algún documento que vence dentro de los próximos \"dias\" (por defecto 30). No requiere autenticación.")
    @GetMapping("/publico/documentos-por-vencer")
    public ResponseEntity<List<Vehiculo>> getVehiculosConDocumentosPorVencer(
            @Parameter(description = "Ventana de días hacia adelante a considerar") @RequestParam(name = "dias", defaultValue = "30") int dias) {
        return ResponseEntity.ok(vehiculoService.findByDocumentosPorVencer(dias));
    }

    @Operation(summary = "[Público] Vehículo completo por placa", description = "Devuelve un vehículo por "
            + "placa junto con sus conductores y documentos asociados. No requiere autenticación.")
    @GetMapping("/publico/placa/{placa}")
    public ResponseEntity<VehiculoCompletoResponse> getCompletoByPlaca(@Parameter(description = "Placa del vehículo") @PathVariable("placa") String placa) {
        return ResponseEntity.ok(vehiculoService.findCompletoByPlaca(placa));
    }
}
