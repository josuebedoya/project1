package com.proyecto1.DTO;

import java.util.List;

import com.proyecto1.Entities.Vehiculo;
import com.proyecto1.Entities.VehiculoDocumento;

// Respuesta
public class VehiculoDetalleResponse {

    private Vehiculo vehiculo;
    private List<VehiculoDocumento> documentos;

    public VehiculoDetalleResponse(Vehiculo vehiculo, List<VehiculoDocumento> documentos) {
        this.vehiculo = vehiculo;
        this.documentos = documentos;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<VehiculoDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<VehiculoDocumento> documentos) {
        this.documentos = documentos;
    }
}
