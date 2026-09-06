package com.proyecto1.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto1.Entities.Documento;

@Repository("IDocumentoRepo")
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findByCodigo(String codigo);

    Page<Documento> findAll(Pageable pageable);
}
