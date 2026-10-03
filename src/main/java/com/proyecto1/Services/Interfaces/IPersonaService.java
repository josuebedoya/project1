package com.proyecto1.Services.Interfaces;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.proyecto1.DTO.PersonaCreadaResponse;
import com.proyecto1.Entities.Persona;

public interface IPersonaService {

    PersonaCreadaResponse crear(Persona persona);

    Persona actualizar(Persona persona);

    Persona findById(Long id);

    List<Persona> consultarPersonas(Pageable pageable);
}