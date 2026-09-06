package com.proyecto1.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto1.Entities.EstadoDocumento;
import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;

@Repository("IVehiculoDocumentoRepo")
public interface VehiculoDocumentoRepository extends JpaRepository<VehiculoDocumento, Long> {

    List<VehiculoDocumento> findByVehiculoId(Long vehiculoId);

    List<VehiculoDocumento> findByDocumentoId(Long documentoId);

    List<VehiculoDocumento> findByEstado(EstadoDocumento estado);

    // Vehiculos que tienen en comun un tipo de documento asociado.
    @Query("select distinct vd.vehiculo from VehiculoDocumento vd where vd.documento.id = :documentoId")
    List<Vehiculo> findVehiculosByDocumentoId(@Param("documentoId") Long documentoId);

    // Vehiculos segun el estado del documento asociado (Habilitado, Vencido, En Verificacion).
    @Query("select distinct vd.vehiculo from VehiculoDocumento vd where vd.estado = :estado")
    List<Vehiculo> findVehiculosByEstado(@Param("estado") EstadoDocumento estado);

    @Transactional
    void deleteByVehiculoId(Long vehiculoId);
}
