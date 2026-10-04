package com.proyecto1.DTO;

import java.time.LocalDate;

import com.proyecto1.Entities.EstadoDocumento;

import jakarta.validation.constraints.NotNull;

// Actualizacion de un documento ya asociado a un vehiculo (identificado por el id de la
// relacion VehiculoDocumento), pudiendo enviar uno o varios a la vez.
public class DocumentoActualizadoRequest {

    @NotNull(message = "El id de la relación vehículo-documento es obligatorio")
    private Long vehiculoDocumentoId;

    @NotNull(message = "La fecha de expedición es obligatoria")
    private LocalDate fechaExpedicion;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El estado del documento es obligatorio")
    private EstadoDocumento estado;

    // Opcional: solo se actualiza si se envía un valor.
    private byte[] archivoPdf;

    public DocumentoActualizadoRequest() {
    }

    public Long getVehiculoDocumentoId() {
        return vehiculoDocumentoId;
    }

    public void setVehiculoDocumentoId(Long vehiculoDocumentoId) {
        this.vehiculoDocumentoId = vehiculoDocumentoId;
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
}
