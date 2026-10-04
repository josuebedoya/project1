package com.proyecto1.Services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.DTO.ParadaRequest;
import com.proyecto1.DTO.RutaConductorResponse;
import com.proyecto1.DTO.TrayectoRutaRequest;
import com.proyecto1.Entities.EstadoConductor;
import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.Persona;
import com.proyecto1.Entities.TipoPersona;
import com.proyecto1.Entities.Trayecto;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.PersonaRepository;
import com.proyecto1.Repository.TrayectoRepository;
import com.proyecto1.Repository.UsuarioRepository;
import com.proyecto1.Repository.VehiculoConductorRepository;
import com.proyecto1.Repository.VehiculoDocumentoRepository;
import com.proyecto1.Repository.VehiculoRepository;
import com.proyecto1.Services.Interfaces.ITrayectoService;

@Service("TrayectoService")
public class TrayectoServiceImpl implements ITrayectoService {

    // ========== INYECCION DE DEPENDENCIAS ==========
    @Autowired
    @Qualifier("ITrayectoRepo")
    private TrayectoRepository trayectoRepository;

    @Autowired
    @Qualifier("IPersonaRepo")
    private PersonaRepository personaRepository;

    @Autowired
    @Qualifier("IVehiculoRepo")
    private VehiculoRepository vehiculoRepository;

    @Autowired
    @Qualifier("IVehiculoConductorRepo")
    private VehiculoConductorRepository vehiculoConductorRepository;

    @Autowired
    @Qualifier("IVehiculoDocumentoRepo")
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    @Autowired
    @Qualifier("IUsuarioRepo")
    private UsuarioRepository usuarioRepository;

    // ====================== LOGS ======================
    private static final Logger logger = LogManager.getLogger(TrayectoServiceImpl.class);

    // Minimo: parada inicial + final. Maximo: inicial + 5 intermedias + final.
    private static final int MIN_PARADAS = 2;
    private static final int MAX_PARADAS = 7;

    // INSERT: crea todas las paradas de una ruta en una sola transaccion. El orden de cada
    // parada se asigna segun su posicion en la lista enviada (0 = inicial, ultima = final).
    @Override
    @Transactional
    public List<Trayecto> crearRuta(TrayectoRutaRequest request) {
        if (request == null || request.getParadas() == null) {
            logger.error("ERROR CREAR_RUTA: LA PETICION O LAS PARADAS SON NULAS!");
            throw new BusinessException("Los datos de la ruta son obligatorios");
        }
        if (request.getParadas().size() < MIN_PARADAS || request.getParadas().size() > MAX_PARADAS) {
            logger.error("ERROR CREAR_RUTA: LA RUTA {} TIENE {} PARADAS!", request.getCodigoRuta(),
                    request.getParadas().size());
            throw new BusinessException("La ruta debe tener entre " + MIN_PARADAS + " y " + MAX_PARADAS
                    + " paradas (una inicial, hasta 5 intermedias y una final)");
        }
        if (trayectoRepository.existsByCodigoRuta(request.getCodigoRuta())) {
            logger.error("ERROR CREAR_RUTA: EL CODIGO DE RUTA {} YA EXISTE!", request.getCodigoRuta());
            throw new BusinessException("Ya existe una ruta registrada con el código " + request.getCodigoRuta());
        }

        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una persona con id " + request.getPersonaId()));
        if (persona.getTipoPersona() != TipoPersona.C) {
            throw new BusinessException("Solo se pueden asociar personas de tipo conductor a un trayecto");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(request.getVehiculoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un vehículo con id " + request.getVehiculoId()));

        usuarioRepository.findByLogin(request.getLoginUsuario())
                .orElseThrow(() -> new BusinessException(
                        "El login de usuario " + request.getLoginUsuario() + " no existe"));

        validarDocumentosHabilitados(vehiculo);
        validarConductorPuedeOperar(persona, vehiculo);

        List<Trayecto> trayectos = new ArrayList<>();
        int orden = 0;
        for (ParadaRequest parada : request.getParadas()) {
            Trayecto trayecto = new Trayecto();
            trayecto.setPersona(persona);
            trayecto.setVehiculo(vehiculo);
            trayecto.setCodigoRuta(request.getCodigoRuta());
            trayecto.setUbicacion(parada.getUbicacion());
            trayecto.setOrdenParada(orden++);
            trayecto.setLatitud(parada.getLatitud());
            trayecto.setLongitud(parada.getLongitud());
            trayecto.setLoginUsuario(request.getLoginUsuario());
            trayectos.add(trayectoRepository.save(trayecto));
        }
        return trayectos;
    }

    // Trayectos de una ruta, en orden (servicio protegido).
    @Override
    public List<Trayecto> findByCodigoRuta(String codigoRuta) {
        List<Trayecto> trayectos = trayectoRepository.findByCodigoRutaOrderByOrdenParadaAsc(codigoRuta);
        if (trayectos.isEmpty()) {
            throw new ResourceNotFoundException("No existe una ruta con código " + codigoRuta);
        }
        return trayectos;
    }

    // Codigos de ruta (agrupados) en los que ha participado un conductor (servicio protegido).
    @Override
    public List<String> findCodigosRutaByConductor(String identificacion) {
        Persona persona = personaRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una persona con identificación " + identificacion));
        return trayectoRepository.findByPersonaId(persona.getId()).stream()
                .map(Trayecto::getCodigoRuta)
                .distinct()
                .toList();
    }

    // Codigos de ruta y conductor asociado, agrupados por vehiculo (servicio protegido).
    @Override
    public List<RutaConductorResponse> findRutasByPlaca(String placa) {
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un vehículo con placa " + placa));

        Map<String, Persona> agrupado = new LinkedHashMap<>();
        for (Trayecto trayecto : trayectoRepository.findByVehiculoPlaca(vehiculo.getPlaca())) {
            agrupado.putIfAbsent(trayecto.getCodigoRuta(), trayecto.getPersona());
        }
        return agrupado.entrySet().stream()
                .map(entry -> new RutaConductorResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    // Trayectos de rutas donde el vehiculo no esta habilitado o el conductor esta restringido
    // para operar (servicio protegido).
    @Override
    public List<Trayecto> findRestringidos() {
        List<Long> vehiculosNoHabilitados = vehiculoDocumentoRepository.findByEstadoNot(EstadoDocumento.HABILITADO)
                .stream()
                .map(vd -> vd.getVehiculo().getId())
                .distinct()
                .toList();
        List<Long> conductoresRestringidos = vehiculoConductorRepository.findByEstado(EstadoConductor.RO)
                .stream()
                .map(vc -> vc.getPersona().getId())
                .distinct()
                .toList();

        if (vehiculosNoHabilitados.isEmpty() && conductoresRestringidos.isEmpty()) {
            return List.of();
        }
        return trayectoRepository.findByVehiculoIdInOrPersonaIdIn(vehiculosNoHabilitados, conductoresRestringidos);
    }

    // Todos los documentos asociados al vehiculo deben estar en estado Habilitado.
    private void validarDocumentosHabilitados(Vehiculo vehiculo) {
        List<VehiculoDocumento> documentos = vehiculoDocumentoRepository.findByVehiculoId(vehiculo.getId());
        boolean habilitado = !documentos.isEmpty()
                && documentos.stream().allMatch(d -> d.getEstado() == EstadoDocumento.HABILITADO);
        if (!habilitado) {
            logger.error("ERROR CREAR_RUTA: EL VEHICULO {} TIENE DOCUMENTOS SIN HABILITAR!", vehiculo.getPlaca());
            throw new BusinessException(
                    "El vehículo " + vehiculo.getPlaca() + " tiene documentos que no están en estado Habilitado");
        }
    }

    // El conductor debe tener una relacion con el vehiculo en estado PO (Puede Operar).
    private void validarConductorPuedeOperar(Persona persona, Vehiculo vehiculo) {
        boolean puedeOperar = vehiculoConductorRepository
                .findByPersonaIdAndVehiculoId(persona.getId(), vehiculo.getId()).stream()
                .anyMatch(relacion -> relacion.getEstado() == EstadoConductor.PO);
        if (!puedeOperar) {
            logger.error("ERROR CREAR_RUTA: EL CONDUCTOR {} NO PUEDE OPERAR EL VEHICULO {}!",
                    persona.getIdentificacion(), vehiculo.getPlaca());
            throw new BusinessException("El conductor " + persona.getIdentificacion()
                    + " no puede operar el vehículo " + vehiculo.getPlaca());
        }
    }
}
