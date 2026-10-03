package com.proyecto1.Services;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.DTO.PersonaCreadaResponse;
import com.proyecto1.Entities.Persona;
import com.proyecto1.Entities.TipoPersona;
import com.proyecto1.Entities.Usuario;
import com.proyecto1.Exception.BusinessException;
import com.proyecto1.Exception.ResourceNotFoundException;
import com.proyecto1.Repository.PersonaRepository;
import com.proyecto1.Repository.UsuarioRepository;
import com.proyecto1.Services.Interfaces.IPersonaService;

@Service("PersonaService")
public class PersonaServiceImpl implements IPersonaService {

    // ========== INYECCION DE DEPENDENCIAS ==========
    @Autowired
    @Qualifier("IPersonaRepo")
    private PersonaRepository personaRepository;

    @Autowired
    @Qualifier("IUsuarioRepo")
    private UsuarioRepository usuarioRepository;

    // ====================== LOGS ======================
    private static final Logger logger = LogManager.getLogger(PersonaServiceImpl.class);

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // INSERT: crea la persona; si es ADMINISTRATIVO crea tambien su usuario en la misma transaccion.
    // Si falla la creacion del usuario, se deshace tambien la persona.
    @Override
    @Transactional
    public PersonaCreadaResponse crear(Persona persona) {
        if (persona == null) {
            logger.error("ERROR CREAR_PERSONA: LA PERSONA ES NULA!");
            throw new BusinessException("Los datos de la persona son obligatorios");
        }
        persona.setId(null);

        personaRepository.findByIdentificacion(persona.getIdentificacion()).ifPresent(p -> {
            logger.error("ERROR CREAR_PERSONA: LA IDENTIFICACION {} YA EXISTE!", persona.getIdentificacion());
            throw new BusinessException("Ya existe una persona con la identificación " + persona.getIdentificacion());
        });

        Persona guardada = personaRepository.save(persona);

        // Los conductores no tienen usuario.
        if (guardada.getTipoPersona() != TipoPersona.A) {
            return new PersonaCreadaResponse(guardada, null, null, null);
        }

        String login = generarLogin(guardada);
        String passwordPlano = generarPassword();
        String apikey = UUID.randomUUID().toString();

        Usuario usuario = new Usuario();
        usuario.setIdpersona(guardada.getId());
        usuario.setPersona(guardada);
        usuario.setLogin(login);
        usuario.setPassword(passwordEncoder.encode(passwordPlano));
        usuario.setApikey(apikey);
        usuarioRepository.save(usuario);

        return new PersonaCreadaResponse(guardada, login, passwordPlano, apikey);
    }

    // UPDATE: actualiza los datos de la persona. No permite cambiar el tipo de persona.
    @Override
    @Transactional
    public Persona actualizar(Persona persona) {
        if (persona == null || persona.getId() == null) {
            logger.error("ERROR EDITAR_PERSONA: LA PERSONA O SU ID ES NULO!");
            throw new BusinessException("La persona y su id son obligatorios para actualizar");
        }
        Persona existente = findById(persona.getId());

        if (existente.getTipoPersona() != persona.getTipoPersona()) {
            throw new BusinessException("No se puede cambiar el tipo de persona");
        }

        personaRepository.findByIdentificacion(persona.getIdentificacion()).ifPresent(p -> {
            if (!p.getId().equals(persona.getId())) {
                throw new BusinessException("Ya existe otra persona con la identificación " + persona.getIdentificacion());
            }
        });

        return personaRepository.save(persona);
    }

    // PERSONA POR ID
    @Override
    public Persona findById(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una persona con id " + id));
    }

    // LISTA DE PERSONAS
    @Override
    public List<Persona> consultarPersonas(Pageable pageable) {
        return personaRepository.findAll(pageable).getContent();
    }

    // Regla de nemotecnia: primera letra del nombre + primera letra del apellido + identificacion.
    private String generarLogin(Persona persona) {
        String inicialNombre = persona.getNombres().trim().substring(0, 1);
        String inicialApellido = persona.getApellidos().trim().substring(0, 1);
        return (inicialNombre + inicialApellido).toLowerCase() + persona.getIdentificacion().trim();
    }

    // Password automatico de 10 caracteres.
    private String generarPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}