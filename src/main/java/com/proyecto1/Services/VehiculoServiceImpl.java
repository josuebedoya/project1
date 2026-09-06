package com.proyecto1.Services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.DTO.DocumentoAsociadoRequest;
import com.proyecto1.DTO.VehiculoRequest;
import com.proyecto1.Entities.Documento;
import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.TipoVehiculo;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.DocumentoRepository;
import com.proyecto1.Repository.VehiculoDocumentoRepository;
import com.proyecto1.Repository.VehiculoRepository;
import com.proyecto1.Services.Interfaces.IVehiculoService;

@Service("VehiculoService")
public class VehiculoServiceImpl implements IVehiculoService {

    // ========== INYECCION DE DEPENDENCIAS ==========
    @Autowired
    @Qualifier("IVehiculoRepo")
    private VehiculoRepository vehiculoRepository;

    @Autowired
    @Qualifier("IDocumentoRepo")
    private DocumentoRepository documentoRepository;

    @Autowired
    @Qualifier("IVehiculoDocumentoRepo")
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    // ====================== LOGS ======================
    private static final Logger logger = LogManager.getLogger(VehiculoServiceImpl.class);

    private static final String REGEX_PLACA_AUTOMOVIL = "^[A-Z]{3}[0-9]{3}$";
    private static final String REGEX_PLACA_MOTOCICLETA = "^[A-Z]{3}[0-9]{2}[A-Z]{1}$";

    // INSERT: crea el vehiculo con, como minimo, un documento asociado en estado En Verificacion.
    @Override
    @Transactional
    public Vehiculo crear(VehiculoRequest request) {
        if (request == null || request.getVehiculo() == null) {
            logger.error("ERROR CREAR_VEHICULO: LA PETICION O EL VEHICULO ES NULO!");
            throw new BusinessException("Los datos del vehículo son obligatorios");
        }
        if (request.getDocumentos() == null || request.getDocumentos().isEmpty()) {
            logger.error("ERROR CREAR_VEHICULO: EL VEHICULO DEBE TENER MINIMO UN DOCUMENTO ASOCIADO!");
            throw new BusinessException("No se puede crear un vehículo sin al menos un documento asociado");
        }

        Vehiculo vehiculo = request.getVehiculo();
        vehiculo.setId(null);
        validarPlaca(vehiculo);

        vehiculoRepository.findByPlaca(vehiculo.getPlaca()).ifPresent(v -> {
            logger.error("ERROR CREAR_VEHICULO: LA PLACA {} YA EXISTE!", vehiculo.getPlaca());
            throw new BusinessException("Ya existe un vehículo registrado con la placa " + vehiculo.getPlaca());
        });

        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        for (DocumentoAsociadoRequest docReq : request.getDocumentos()) {
            asociarDocumento(vehiculoGuardado, docReq);
        }

        return vehiculoGuardado;
    }

    // UPDATE: solo actualiza los datos propios del vehiculo, no sus documentos.
    @Override
    @Transactional
    public Vehiculo actualizar(Vehiculo vehiculo) {
        if (vehiculo == null || vehiculo.getId() == null) {
            logger.error("ERROR EDITAR_VEHICULO: EL VEHICULO ES NULO O EL ID ES NULO!");
            throw new BusinessException("El vehículo y su id son obligatorios para actualizar");
        }
        findById(vehiculo.getId());
        validarPlaca(vehiculo);

        vehiculoRepository.findByPlaca(vehiculo.getPlaca()).ifPresent(v -> {
            if (!v.getId().equals(vehiculo.getId())) {
                throw new BusinessException("Ya existe otro vehículo con la placa " + vehiculo.getPlaca());
            }
        });

        return vehiculoRepository.save(vehiculo);
    }

    // DELETE: elimina primero las asociaciones con documentos y luego el vehiculo.
    @Override
    @Transactional
    public void eliminar(Long id) {
        Vehiculo vehiculo = findById(id);
        vehiculoDocumentoRepository.deleteByVehiculoId(id);
        vehiculoRepository.delete(vehiculo);
    }

    // LISTA DE VEHICULOS
    @Override
    public List<Vehiculo> consultarVehiculos(Pageable pageable) {
        return vehiculoRepository.findAll(pageable).getContent();
    }

    // VEHICULO POR ID
    @Override
    public Vehiculo findById(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un vehículo con id " + id));
    }

    // ================ DOCUMENTOS ASOCIADOS ================
    @Override
    public List<VehiculoDocumento> findDocumentosByVehiculoId(Long vehiculoId) {
        findById(vehiculoId);
        return vehiculoDocumentoRepository.findByVehiculoId(vehiculoId);
    }

    @Override
    @Transactional
    public List<VehiculoDocumento> agregarDocumentos(Long vehiculoId, List<DocumentoAsociadoRequest> documentos) {
        Vehiculo vehiculo = findById(vehiculoId);
        if (documentos == null || documentos.isEmpty()) {
            logger.error("ERROR AGREGAR_DOCUMENTOS: LA LISTA DE DOCUMENTOS A ASOCIAR ES NULA O VACIA!");
            throw new BusinessException("Debe enviar al menos un documento para asociar");
        }
        List<VehiculoDocumento> asociados = new ArrayList<>();
        for (DocumentoAsociadoRequest docReq : documentos) {
            asociados.add(asociarDocumento(vehiculo, docReq));
        }
        return asociados;
    }

    private VehiculoDocumento asociarDocumento(Vehiculo vehiculo, DocumentoAsociadoRequest docReq) {
        Documento documento = documentoRepository.findById(docReq.getDocumentoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un documento con id " + docReq.getDocumentoId()));

        VehiculoDocumento vehiculoDocumento = new VehiculoDocumento();
        vehiculoDocumento.setVehiculo(vehiculo);
        vehiculoDocumento.setDocumento(documento);
        vehiculoDocumento.setFechaExpedicion(docReq.getFechaExpedicion());
        vehiculoDocumento.setFechaVencimiento(docReq.getFechaVencimiento());
        // Regla de negocio: todo documento asociado nace en estado "En Verificacion".
        vehiculoDocumento.setEstado(EstadoDocumento.EN_VERIFICACION);

        return vehiculoDocumentoRepository.save(vehiculoDocumento);
    }

    // ================ BUSQUEDAS ================
    @Override
    public Vehiculo findByPlaca(String placa) {
        if (placa == null) {
            throw new BusinessException("La placa es obligatoria");
        }
        return vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un vehículo con placa " + placa));
    }

    @Override
    public List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipoVehiculo) {
        return vehiculoRepository.findByTipoVehiculo(tipoVehiculo);
    }

    @Override
    public List<Vehiculo> findByDocumentoId(Long documentoId) {
        documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un documento con id " + documentoId));
        return vehiculoDocumentoRepository.findVehiculosByDocumentoId(documentoId);
    }

    @Override
    public List<Vehiculo> findByEstadoDocumento(EstadoDocumento estado) {
        return vehiculoDocumentoRepository.findVehiculosByEstado(estado);
    }

    // Valida el formato de placa segun el tipo de vehiculo (regla que depende de dos campos a la vez,
    // por lo que no se puede resolver con una unica anotacion Bean Validation).
    private void validarPlaca(Vehiculo vehiculo) {
        if (vehiculo.getPlaca() == null || vehiculo.getTipoVehiculo() == null) {
            throw new BusinessException("La placa y el tipo de vehículo son obligatorios");
        }
        String placa = vehiculo.getPlaca().toUpperCase();
        vehiculo.setPlaca(placa);

        if (vehiculo.getTipoVehiculo() == TipoVehiculo.AUTOMOVIL && !placa.matches(REGEX_PLACA_AUTOMOVIL)) {
            throw new BusinessException(
                    "La placa de un automóvil debe tener 3 letras seguidas de 3 números, ejemplo ABC123");
        }
        if (vehiculo.getTipoVehiculo() == TipoVehiculo.MOTOCICLETA && !placa.matches(REGEX_PLACA_MOTOCICLETA)) {
            throw new BusinessException(
                    "La placa de una motocicleta debe tener 3 letras, 2 números y terminar en letra, ejemplo ABC12D");
        }
    }
}
