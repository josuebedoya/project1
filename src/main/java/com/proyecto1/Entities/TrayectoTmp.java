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

// Tabla de staging para el cargue masivo de trayectos vía Excel (Fase 4). Replica los campos
// de Trayecto pero como texto plano (identificacion/placa/valores crudos de la celda), ya que
// el procedimiento de cargue traslada el archivo "sin ningun tipo de alteracion"; la validacion
// de tipos/existencia ocurre despues, en el procedimiento de validacion.
@Entity
@Table(name = "Trayecto_tmp", schema = "PPOOII")
@Check(constraints = "Estado IN ('CARGADO','VALIDADO','PROCESADO','ERROR')")
public class TrayectoTmp implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    // Identifica los registros de un mismo cargue de Excel: yyyyMMddHHmm del momento de la carga.
    @Column(name = "IdCargue", nullable = false)
    private Long idCargue;

    @Column(name = "PersonaIdentificacion", length = 20)
    private String personaIdentificacion;

    @Column(name = "VehiculoPlaca", length = 6)
    private String vehiculoPlaca;

    @Column(name = "CodigoRuta", length = 30)
    private String codigoRuta;

    @Column(name = "Ubicacion", length = 255)
    private String ubicacion;

    @Column(name = "OrdenParada", length = 10)
    private String ordenParada;

    @Column(name = "Latitud", length = 30)
    private String latitud;

    @Column(name = "Longitud", length = 30)
    private String longitud;

    @Column(name = "LoginUsuario", length = 30)
    private String loginUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "Estado", nullable = false, length = 10)
    private EstadoTrayectoTmp estado;

    // Acumulativo: cada regla de validacion que falla agrega su propio mensaje.
    @Column(name = "Observacion", columnDefinition = "TEXT")
    private String observacion;

    public TrayectoTmp() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCargue() {
        return idCargue;
    }

    public void setIdCargue(Long idCargue) {
        this.idCargue = idCargue;
    }

    public String getPersonaIdentificacion() {
        return personaIdentificacion;
    }

    public void setPersonaIdentificacion(String personaIdentificacion) {
        this.personaIdentificacion = personaIdentificacion;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
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

    public String getOrdenParada() {
        return ordenParada;
    }

    public void setOrdenParada(String ordenParada) {
        this.ordenParada = ordenParada;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public EstadoTrayectoTmp getEstado() {
        return estado;
    }

    public void setEstado(EstadoTrayectoTmp estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "TrayectoTmp [id=" + id + ", idCargue=" + idCargue + ", personaIdentificacion="
                + personaIdentificacion + ", vehiculoPlaca=" + vehiculoPlaca + ", codigoRuta=" + codigoRuta
                + ", estado=" + estado + ", observacion=" + observacion + "]";
    }
}
