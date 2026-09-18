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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Datos basicos de una persona que representa un conductor o un administrador del sistema.
@Entity
@Table(name = "Persona", schema = "PPOOII", uniqueConstraints = @UniqueConstraint(name = "UQ_PERSONA_IDENTIFICACION", columnNames = "Identificacion"))
@Check(constraints = "TipoIdentificacion IN ('CC') AND TipoPersona IN ('C','A')")
public class Persona implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 20, message = "La identificación no puede superar 20 caracteres")
    @Column(name = "Identificacion", nullable = false, length = 20, unique = true)
    private String identificacion;

    @NotNull(message = "El tipo de identificación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "TipoIdentificacion", nullable = false, length = 2)
    private TipoIdentificacion tipoIdentificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    @Column(name = "Nombres", nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(name = "Apellidos", nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Column(name = "CorreoElectronico", nullable = false, length = 150)
    private String correoElectronico;

    @NotNull(message = "El tipo de persona es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "TipoPersona", nullable = false, length = 1)
    private TipoPersona tipoPersona;

    public Persona() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public TipoIdentificacion getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    @Override
    public String toString() {
        return "Persona [id=" + id + ", identificacion=" + identificacion + ", tipoIdentificacion="
                + tipoIdentificacion + ", nombres=" + nombres + ", apellidos=" + apellidos + ", correoElectronico="
                + correoElectronico + ", tipoPersona=" + tipoPersona + "]";
    }
}
