package com.proyecto1.DTO;

import java.util.List;

import com.proyecto1.Entities.Vehiculo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Body para crear un vehiculo: exige al menos un documento asociado (regla de negocio del enunciado).
public class VehiculoRequest {

    @NotNull(message = "Los datos del vehículo son obligatorios")
    @Valid
    private Vehiculo vehiculo;

    @NotEmpty(message = "El vehículo debe tener como mínimo un documento asociado")
    @Valid
    private List<DocumentoAsociadoRequest> documentos;

    public VehiculoRequest() {
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<DocumentoAsociadoRequest> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoAsociadoRequest> documentos) {
        this.documentos = documentos;
    }
}
