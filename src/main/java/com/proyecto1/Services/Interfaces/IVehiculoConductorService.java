package com.proyecto1.Services.Interfaces;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.proyecto1.DTO.VehiculoConductorRequest;
import com.proyecto1.Entities.EstadoConductor;
import com.proyecto1.Entities.VehiculoConductor;

public interface IVehiculoConductorService {

    VehiculoConductor asociar(VehiculoConductorRequest request);

    VehiculoConductor cambiarEstado(Long id, EstadoConductor estado);

    VehiculoConductor findById(Long id);

    List<VehiculoConductor> consultar(Pageable pageable);

    // ============== SERVICIO PUBLICO ==============
    List<VehiculoConductor> findConductoresQuePuedenOperar();
}
