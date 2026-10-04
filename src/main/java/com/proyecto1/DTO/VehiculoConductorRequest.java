package com.proyecto1.DTO;

import java.time.LocalDate;

import com.proyecto1.Entities.EstadoConductor;

import jakarta.validation.constraints.NotNull;

// Body para asociar un conductor a un vehiculo (los vehiculos que puede operar).
public class VehiculoConductorRequest {

    @NotNull(message = "El id de la persona (conductor) es obligatorio")
    private Long personaId;

    @NotNull(message = "El id del vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "La fecha de asociación es obligatoria")
    private LocalDate fechaAsociacion;

    @NotNull(message = "El estado del conductor es obligatorio")
    private EstadoConductor estado;

    public VehiculoConductorRequest() {
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public LocalDate getFechaAsociacion() {
        return fechaAsociacion;
    }

    public void setFechaAsociacion(LocalDate fechaAsociacion) {
        this.fechaAsociacion = fechaAsociacion;
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        this.estado = estado;
    }
}
