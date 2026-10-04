package com.proyecto1.Services.Interfaces;

import java.util.List;

import com.proyecto1.DTO.RutaConductorResponse;
import com.proyecto1.DTO.TrayectoRutaRequest;
import com.proyecto1.Entities.Trayecto;

public interface ITrayectoService {

    List<Trayecto> crearRuta(TrayectoRutaRequest request);

    List<Trayecto> findByCodigoRuta(String codigoRuta);

    List<String> findCodigosRutaByConductor(String identificacion);

    List<RutaConductorResponse> findRutasByPlaca(String placa);

    List<Trayecto> findRestringidos();
}
