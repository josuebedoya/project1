package com.proyecto1.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto1.Entities.Trayecto;

@Repository("ITrayectoRepo")
public interface TrayectoRepository extends JpaRepository<Trayecto, Long> {

    boolean existsByCodigoRuta(String codigoRuta);

    List<Trayecto> findByCodigoRutaOrderByOrdenParadaAsc(String codigoRuta);

    // Codigos de ruta en los que ha participado un conductor (servicio protegido).
    List<Trayecto> findByPersonaId(Long personaId);

    // Rutas y conductores asociados a un vehiculo especifico (servicio protegido).
    List<Trayecto> findByVehiculoPlaca(String placa);

    // Trayectos pendientes de geocodificar (tarea programada cada 90 segundos).
    List<Trayecto> findByLatitudIsNullOrLongitudIsNull();

    // Trayectos de rutas donde el vehiculo no esta habilitado o el conductor esta restringido.
    List<Trayecto> findByVehiculoIdInOrPersonaIdIn(List<Long> vehiculoIds, List<Long> personaIds);
}
