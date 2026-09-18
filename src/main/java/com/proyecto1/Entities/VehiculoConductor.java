package com.proyecto1.Entities;

import java.io.Serializable;
import java.time.LocalDate;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

// Relacion N:M entre Vehiculo y Persona (unicamente personas de tipo CONDUCTOR).
// Un vehiculo tiene como minimo un conductor asociado o muchos conductores.
@Entity
@Table(name = "VehiculoConductor", schema = "PPOOII")
@Check(constraints = "Estado IN ('PO','EA','RO')")
public class VehiculoConductor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "VehiculoId", nullable = false, foreignKey = @ForeignKey(name = "FK_VEHICULOCONDUCTOR_VEHICULO"))
    private Vehiculo vehiculo;

    // Debe referenciar unicamente a una Persona con TipoPersona = C (Conductor); se valida en el service.
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PersonaId", nullable = false, foreignKey = @ForeignKey(name = "FK_VEHICULOCONDUCTOR_PERSONA"))
    private Persona persona;

    @NotNull(message = "La fecha de asociación del conductor al vehículo es obligatoria")
    @Column(name = "FechaAsociacion", nullable = false)
    private LocalDate fechaAsociacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Estado", nullable = false, length = 2)
    private EstadoConductor estado;

    public VehiculoConductor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
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

    @Override
    public String toString() {
        return "VehiculoConductor [id=" + id + ", vehiculoId=" + (vehiculo != null ? vehiculo.getId() : null)
                + ", personaId=" + (persona != null ? persona.getId() : null) + ", fechaAsociacion="
                + fechaAsociacion + ", estado=" + estado + "]";
    }
}
