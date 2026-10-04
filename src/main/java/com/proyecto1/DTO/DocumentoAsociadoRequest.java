package com.proyecto1.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

// Datos
public class DocumentoAsociadoRequest {

    @NotNull(message = "El id del documento es obligatorio")
    private Long documentoId;

    @NotNull(message = "La fecha de expedición es obligatoria")
    private LocalDate fechaExpedicion;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    // Opcional: documento PDF en BASE64 (Jackson lo decodifica automaticamente a byte[]).
    private byte[] archivoPdf;

    public DocumentoAsociadoRequest() {
    }

    public Long getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Long documentoId) {
        this.documentoId = documentoId;
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

    public byte[] getArchivoPdf() {
        return archivoPdf;
    }

    public void setArchivoPdf(byte[] archivoPdf) {
        this.archivoPdf = archivoPdf;
    }
}
