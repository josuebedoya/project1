package com.proyecto1.DTO;

import java.util.List;

import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoConductor;
import com.proyecto1.Entities.VehiculoDocumento;

// Respuesta publica: vehiculo por placa con sus documentos y conductores asociados.
public record VehiculoCompletoResponse(Vehiculo vehiculo, List<VehiculoDocumento> documentos,
        List<VehiculoConductor> conductores) {
}
