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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/proyecto1/documento")
public class DocumentoController {

    // ==========INYECCION DEL SERVICE==========
    @Autowired
    @Qualifier("DocumentoService")
    private IDocumentoService documentoService;

    // ==========METODOS HTTP CRUD==================
    @PostMapping
    public ResponseEntity<Documento> agregarDocumento(@RequestBody @Valid Documento documento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.crear(documento));
    }

    @PutMapping
    public ResponseEntity<Documento> editarDocumento(@RequestBody @Valid Documento documento) {
        return ResponseEntity.ok(documentoService.actualizar(documento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDocumento(@PathVariable("id") Long id) {
        documentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Documento> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Documento>> listadoDocumentos(Pageable pageable) {
        return ResponseEntity.ok(documentoService.consultarDocumentos(pageable));
    }
}
