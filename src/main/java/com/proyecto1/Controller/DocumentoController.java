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

import com.proyecto1.Entities.Documento;
import com.proyecto1.Services.Interfaces.IDocumentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/documento")
@Tag(name = "Documento", description = "CRUD de la entidad paramétrica de documentos (SOAT, Tecnomecánica, etc.) (Fase 1)")
@SecurityRequirement(name = "bearerAuth")
public class DocumentoController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("DocumentoService")
    private IDocumentoService documentoService;

    // ==========METODOS HTTP CRUD==================
    @Operation(summary = "Crear documento parametrizado", description = "Crea un nuevo tipo de documento "
            + "(código, nombre, a qué tipo de vehículo aplica y si es obligatorio).")
    @PostMapping
    public ResponseEntity<Documento> agregarDocumento(@RequestBody @Valid Documento documento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.crear(documento));
    }

    @Operation(summary = "Editar documento parametrizado", description = "Actualiza los datos de un "
            + "documento paramétrico existente (el id va dentro del body).")
    @PutMapping
    public ResponseEntity<Documento> editarDocumento(@RequestBody @Valid Documento documento) {
        return ResponseEntity.ok(documentoService.actualizar(documento));
    }

    @Operation(summary = "Eliminar documento parametrizado", description = "Elimina un documento paramétrico por id.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@Parameter(description = "Id del documento") @PathVariable("id") Long id) {
        documentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar documento por id")
    @GetMapping("/{id}")
    public ResponseEntity<Documento> getById(@Parameter(description = "Id del documento") @PathVariable("id") Long id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @Operation(summary = "Listar documentos", description = "Listado paginado de todos los documentos parametrizados.")
    @GetMapping
    public ResponseEntity<List<Documento>> listadoDocumentos(Pageable pageable) {
        return ResponseEntity.ok(documentoService.consultarDocumentos(pageable));
    }
}
