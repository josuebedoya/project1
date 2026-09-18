package com.proyecto1.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.Entities.EstadoConductor;
import com.proyecto1.Entities.VehiculoConductor;

@Repository("IVehiculoConductorRepo")
public interface VehiculoConductorRepository extends JpaRepository<VehiculoConductor, Long> {

    List<VehiculoConductor> findByVehiculoId(Long vehiculoId);

    List<VehiculoConductor> findByPersonaId(Long personaId);

    // Conductores que pueden operar (servicio publico).
    List<VehiculoConductor> findByEstado(EstadoConductor estado);

    @Query("select vc from VehiculoConductor vc where vc.persona.id = :personaId and vc.vehiculo.id = :vehiculoId")
    List<VehiculoConductor> findByPersonaIdAndVehiculoId(@Param("personaId") Long personaId,
            @Param("vehiculoId") Long vehiculoId);

    @Transactional
    void deleteByVehiculoId(Long vehiculoId);
}
