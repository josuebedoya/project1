package com.proyecto1.Entities;

import java.io.Serializable;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Vehiculo", schema = "PPOOII", uniqueConstraints = @UniqueConstraint(name = "UQ_VEHICULO_PLACA", columnNames = "Placa"))
@Check(constraints = "TipoVehiculo IN ('AUTOMOVIL','MOTOCICLETA') "
        + "AND TipoServicio IN ('Pu','Pr') "
        + "AND TipoCombustible IN ('GASOLINA','GAS','DISEL')")
public class Vehiculo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "TipoVehiculo", nullable = false, length = 15)
    private TipoVehiculo tipoVehiculo;

    // Automovil: 3 letras + 3 numeros. Motocicleta: 3 letras + 2 numeros + 1 letra.
    // El formato exacto depende del tipo de vehiculo y se valida en el service.
    @NotBlank(message = "La placa es obligatoria")
    @Size(min = 6, max = 6, message = "La placa debe tener exactamente 6 caracteres")
    @Column(name = "Placa", nullable = false, length = 6, unique = true)
    private String placa;

    @NotNull(message = "El tipo de servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "TipoServicio", nullable = false, length = 2)
    private TipoServicio tipoServicio;

    @NotNull(message = "El tipo de combustible es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "TipoCombustible", nullable = false, length = 10)
    private TipoCombustible tipoCombustible;

    @NotNull(message = "La capacidad de pasajeros es obligatoria")
    @Positive(message = "La capacidad de pasajeros debe ser mayor a 0")
    @Column(name = "CapacidadPasajeros", nullable = false)
    private Integer capacidadPasajeros;

    @NotBlank(message = "El color es obligatorio")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color debe ser un código hexadecimal válido, ejemplo #FFFFFF")
    @Column(name = "Color", nullable = false, length = 7)
    private String color;

    @NotNull(message = "El modelo es obligatorio")
    @Column(name = "Modelo", nullable = false)
    private Integer modelo;

    @NotBlank(message = "La marca es obligatoria")
    @Column(name = "Marca", nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "La línea es obligatoria")
    @Column(name = "Linea", nullable = false, length = 50)
    private String linea;

    public Vehiculo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustible tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public Integer getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(Integer capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getModelo() {
        return modelo;
    }

    public void setModelo(Integer modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    @Override
    public String toString() {
        return "Vehiculo [id=" + id + ", tipoVehiculo=" + tipoVehiculo + ", placa=" + placa + ", tipoServicio="
                + tipoServicio + ", tipoCombustible=" + tipoCombustible + ", capacidadPasajeros="
                + capacidadPasajeros + ", color=" + color + ", modelo=" + modelo + ", marca=" + marca + ", linea="
                + linea + "]";
    }
}
