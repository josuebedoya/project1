package com.proyecto1.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto1.Entities.TipoVehiculo;
import com.proyecto1.Entities.Vehiculo;

@Repository("IVehiculoRepo")
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipoVehiculo);

    Page<Vehiculo> findAll(Pageable pageable);
}
