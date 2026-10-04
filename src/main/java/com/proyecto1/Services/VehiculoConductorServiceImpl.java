package com.proyecto1.Services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.DTO.VehiculoConductorRequest;
import com.proyecto1.Entities.EstadoConductor;
import com.proyecto1.Entities.Persona;
import com.proyecto1.Entities.TipoPersona;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoConductor;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.PersonaRepository;
import com.proyecto1.Repository.VehiculoConductorRepository;
import com.proyecto1.Repository.VehiculoRepository;
import com.proyecto1.Services.Interfaces.IVehiculoConductorService;

@Service("VehiculoConductorService")
public class VehiculoConductorServiceImpl implements IVehiculoConductorService {

    // ========== INYECCION DE DEPENDENCIAS ==========
    @Autowired
    @Qualifier("IVehiculoConductorRepo")
    private VehiculoConductorRepository vehiculoConductorRepository;

    @Autowired
    @Qualifier("IPersonaRepo")
    private PersonaRepository personaRepository;

    @Autowired
    @Qualifier("IVehiculoRepo")
    private VehiculoRepository vehiculoRepository;

    // ====================== LOGS ======================
    private static final Logger logger = LogManager.getLogger(VehiculoConductorServiceImpl.class);

    // INSERT: asocia un conductor a un vehiculo (los vehiculos que puede operar).
    @Override
    @Transactional
    public VehiculoConductor asociar(VehiculoConductorRequest request) {
        if (request == null) {
            logger.error("ERROR ASOCIAR_CONDUCTOR: LA PETICION ES NULA!");
            throw new BusinessException("Los datos de la asociación son obligatorios");
        }
        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una persona con id " + request.getPersonaId()));
        if (persona.getTipoPersona() != TipoPersona.C) {
            logger.error("ERROR ASOCIAR_CONDUCTOR: LA PERSONA {} NO ES CONDUCTOR!", persona.getIdentificacion());
            throw new BusinessException("Solo se pueden asociar personas de tipo conductor a un vehículo");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un vehículo con id " + request.getVehiculoId()));

        boolean yaAsociado = !vehiculoConductorRepository
                .findByPersonaIdAndVehiculoId(persona.getId(), vehiculo.getId()).isEmpty();
        if (yaAsociado) {
            throw new BusinessException("El conductor " + persona.getIdentificacion()
                    + " ya está asociado al vehículo " + vehiculo.getPlaca());
        }

        VehiculoConductor relacion = new VehiculoConductor();
        relacion.setPersona(persona);
        relacion.setVehiculo(vehiculo);
        relacion.setFechaAsociacion(request.getFechaAsociacion());
        relacion.setEstado(request.getEstado());
        return vehiculoConductorRepository.save(relacion);
    }

    // UPDATE: cambia el estado del conductor en relacion con el vehiculo (PO, EA, RO).
    @Override
    @Transactional
    public VehiculoConductor cambiarEstado(Long id, EstadoConductor estado) {
        if (estado == null) {
            throw new BusinessException("El estado del conductor es obligatorio");
        }
        VehiculoConductor relacion = findById(id);
        relacion.setEstado(estado);
        return vehiculoConductorRepository.save(relacion);
    }

    @Override
    public VehiculoConductor findById(Long id) {
        return vehiculoConductorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una relación conductor-vehículo con id " + id));
    }

    @Override
    public List<VehiculoConductor> consultar(Pageable pageable) {
        return vehiculoConductorRepository.findAll(pageable).getContent();
    }

    // Conductores que pueden operar (servicio publico).
    @Override
    public List<VehiculoConductor> findConductoresQuePuedenOperar() {
        return vehiculoConductorRepository.findByEstado(EstadoConductor.PO);
    }
}
