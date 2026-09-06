package com.proyecto1.Services.Interfaces;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.proyecto1.Entities.Documento;

public interface IDocumentoService {

    Documento crear(Documento documento);

    Documento actualizar(Documento documento);

    void eliminar(Long id);

    List<Documento> consultarDocumentos(Pageable pageable);

    Documento findById(Long id);
}
