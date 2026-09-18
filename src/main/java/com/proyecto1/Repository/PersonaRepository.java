package com.proyecto1.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.proyecto1.Entities.Persona;
import com.proyecto1.Entities.TipoPersona;

@Repository("IPersonaRepo")
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByIdentificacion(String identificacion);

    List<Persona> findByTipoPersona(TipoPersona tipoPersona);

    Page<Persona> findAll(Pageable pageable);

    // Total de personas agrupadas por tipo (servicio publico).
    @Query("select p.tipoPersona as tipoPersona, count(p) as total from Persona p group by p.tipoPersona")
    List<Object[]> countByTipoPersona();
}
