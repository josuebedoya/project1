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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

// Relacion N:M entre Vehiculo y Documento con atributos propios de la asociacion.
@Entity
@Table(name = "VehiculoDocumento", schema = "PPOOII")
@Check(constraints = "Estado IN ('HABILITADO','VENCIDO','EN_VERIFICACION')")
public class VehiculoDocumento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    // EAGER para poder serializar en JSON sin depender de un contexto transaccional abierto.
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "VehiculoId", nullable = false, foreignKey = @ForeignKey(name = "FK_VEHICULODOCUMENTO_VEHICULO"))
    private Vehiculo vehiculo;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "DocumentoId", nullable = false, foreignKey = @ForeignKey(name = "FK_VEHICULODOCUMENTO_DOCUMENTO"))
    private Documento documento;

    @NotNull(message = "La fecha de expedición del documento es obligatoria")
    @Column(name = "FechaExpedicion", nullable = false)
    private LocalDate fechaExpedicion;

    @NotNull(message = "La fecha de vencimiento del documento es obligatoria")
    @Column(name = "FechaVencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Estado", nullable = false, length = 20)
    private EstadoDocumento estado;

    // Documento PDF asociado, registrado en BASE64 y almacenado en un campo BLOB.
    @Lob
    @Column(name = "ArchivoPdf", columnDefinition = "LONGBLOB")
    private byte[] archivoPdf;

    public VehiculoDocumento() {
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

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public LocalDate getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(LocalDate fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoDocumento getEstado() {
        return estado;
    }

    public void setEstado(EstadoDocumento estado) {
        this.estado = estado;
    }

    public byte[] getArchivoPdf() {
        return archivoPdf;
    }

    public void setArchivoPdf(byte[] archivoPdf) {
        this.archivoPdf = archivoPdf;
    }

    @Override
    public String toString() {
        return "VehiculoDocumento [id=" + id + ", vehiculoId=" + (vehiculo != null ? vehiculo.getId() : null)
                + ", documentoId=" + (documento != null ? documento.getId() : null) + ", fechaExpedicion="
                + fechaExpedicion + ", fechaVencimiento=" + fechaVencimiento + ", estado=" + estado + "]";
    }
}
