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
import jakarta.validation.constraints.Size;

// Entidad parametrica/de configuracion: documentos que pueden asociarse a los vehiculos.
@Entity
@Table(name = "Documento", schema = "PPOOII", uniqueConstraints = @UniqueConstraint(name = "UQ_DOCUMENTO_CODIGO", columnNames = "Codigo"))
@Check(constraints = "TipoVehiculoAplica IN ('A','M','AM') AND Obligatoriedad IN ('RA','RM','RR')")
public class Documento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotBlank(message = "El código del documento es obligatorio")
    @Size(max = 20, message = "El código del documento no puede superar 20 caracteres")
    @Column(name = "Codigo", nullable = false, length = 20, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre del documento es obligatorio")
    @Column(name = "Nombre", nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El tipo de vehículo al que aplica el documento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "TipoVehiculoAplica", nullable = false, length = 2)
    private TipoVehiculoAplica tipoVehiculoAplica;

    @NotNull(message = "La obligatoriedad del documento es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "Obligatoriedad", nullable = false, length = 2)
    private Obligatoriedad obligatoriedad;

    @Column(name = "Descripcion", length = 255)
    private String descripcion;

    public Documento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoVehiculoAplica getTipoVehiculoAplica() {
        return tipoVehiculoAplica;
    }

    public void setTipoVehiculoAplica(TipoVehiculoAplica tipoVehiculoAplica) {
        this.tipoVehiculoAplica = tipoVehiculoAplica;
    }

    public Obligatoriedad getObligatoriedad() {
        return obligatoriedad;
    }

    public void setObligatoriedad(Obligatoriedad obligatoriedad) {
        this.obligatoriedad = obligatoriedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Documento [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", tipoVehiculoAplica="
                + tipoVehiculoAplica + ", obligatoriedad=" + obligatoriedad + ", descripcion=" + descripcion + "]";
    }
}
