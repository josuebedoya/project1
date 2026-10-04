package com.proyecto1.Entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// Representa una parada (inicial, intermedia o final) dentro de la ruta realizada por un
// conductor con un vehiculo especifico. Varias filas comparten el mismo CodigoRuta: la fila
// con OrdenParada = 0 es la parada inicial y la de mayor OrdenParada es la parada final.
@Entity
@Table(name = "Trayecto", schema = "PPOOII",
        uniqueConstraints = @UniqueConstraint(name = "UQ_TRAYECTO_RUTA_ORDEN", columnNames = { "CodigoRuta", "OrdenParada" }))
public class Trayecto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    // Debe referenciar unicamente a una Persona con TipoPersona = C (Conductor); se valida en el service.
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PersonaId", nullable = false, foreignKey = @ForeignKey(name = "FK_TRAYECTO_PERSONA"))
    private Persona persona;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "VehiculoId", nullable = false, foreignKey = @ForeignKey(name = "FK_TRAYECTO_VEHICULO"))
    private Vehiculo vehiculo;

    @NotBlank(message = "El código de ruta es obligatorio")
    @Column(name = "CodigoRuta", nullable = false, length = 30)
    private String codigoRuta;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(name = "Ubicacion", nullable = false, length = 255)
    private String ubicacion;

    // 0 = parada inicial; el valor mayor de cada CodigoRuta = parada final; los intermedios
    // corresponden a las paradas intermedias.
    @NotNull(message = "El orden de la parada es obligatorio")
    @PositiveOrZero(message = "El orden de la parada no puede ser negativo")
    @Column(name = "OrdenParada", nullable = false)
    private Integer ordenParada;

    // Se completan de forma asincrona por la tarea programada de geocodificacion (Google Maps).
    @Column(name = "Latitud")
    private Double latitud;

    @Column(name = "Longitud")
    private Double longitud;

    @NotBlank(message = "El login de usuario que registra el trayecto es obligatorio")
    @Column(name = "LoginUsuario", nullable = false, length = 30)
    private String loginUsuario;

    public Trayecto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getOrdenParada() {
        return ordenParada;
    }

    public void setOrdenParada(Integer ordenParada) {
        this.ordenParada = ordenParada;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    @Override
    public String toString() {
        return "Trayecto [id=" + id + ", personaId=" + (persona != null ? persona.getId() : null) + ", vehiculoId="
                + (vehiculo != null ? vehiculo.getId() : null) + ", codigoRuta=" + codigoRuta + ", ubicacion="
                + ubicacion + ", ordenParada=" + ordenParada + ", latitud=" + latitud + ", longitud=" + longitud
                + ", loginUsuario=" + loginUsuario + "]";
    }
}
