package com.proyecto1.Services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.Entities.Documento;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.DocumentoRepository;
import com.proyecto1.Services.Interfaces.IDocumentoService;

@Service("DocumentoService")
public class DocumentoServiceImpl implements IDocumentoService {

    // ========== INYECCION DE DEPENDENCIAS ==========
    @Autowired
    @Qualifier("IDocumentoRepo")
    private DocumentoRepository documentoRepository;

    // ====================== LOGS ======================
    private static final Logger logger = LogManager.getLogger(DocumentoServiceImpl.class);

    // INSERT
    @Override
    @Transactional
    public Documento crear(Documento documento) {
        if (documento == null) {
            logger.error("ERROR CREAR_DOCUMENTO: EL DOCUMENTO ES NULO!");
            throw new BusinessException("El documento no puede ser nulo");
        }
        documentoRepository.findByCodigo(documento.getCodigo()).ifPresent(d -> {
            logger.error("ERROR CREAR_DOCUMENTO: EL CODIGO {} YA EXISTE", documento.getCodigo());
            throw new BusinessException("Ya existe un documento parametrizado con el código " + documento.getCodigo());
        });
        return documentoRepository.save(documento);
    }

    // UPDATE
    @Override
    @Transactional
    public Documento actualizar(Documento documento) {
        if (documento == null || documento.getId() == null) {
            logger.error("ERROR EDITAR_DOCUMENTO: EL DOCUMENTO ES NULO O EL ID ES NULO!");
            throw new BusinessException("El documento y su id son obligatorios para actualizar");
        }
        findById(documento.getId());
        documentoRepository.findByCodigo(documento.getCodigo()).ifPresent(d -> {
            if (!d.getId().equals(documento.getId())) {
                throw new BusinessException("Ya existe otro documento con el código " + documento.getCodigo());
            }
        });
        return documentoRepository.save(documento);
    }

    // DELETE
    @Override
    @Transactional
    public void eliminar(Long id) {
        Documento documento = findById(id);
        documentoRepository.delete(documento);
    }

    // LISTA DE DOCUMENTOS
    @Override
    public List<Documento> consultarDocumentos(Pageable pageable) {
        return documentoRepository.findAll(pageable).getContent();
    }

    // DOCUMENTO POR ID
    @Override
    public Documento findById(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un documento con id " + id));
    }
}
