package com.proyecto1.DTO;

import com.proyecto1.Entities.EstadoConductor;

import jakarta.validation.constraints.NotNull;

// Body para cambiar el estado del conductor en relacion con el vehiculo (PO, EA, RO).
public class CambiarEstadoConductorRequest {

    @NotNull(message = "El estado del conductor es obligatorio")
    private EstadoConductor estado;

    public CambiarEstadoConductorRequest() {
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }
}
