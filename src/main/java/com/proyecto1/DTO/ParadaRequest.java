package com.proyecto1.DTO;

import jakarta.validation.constraints.NotBlank;

// Una parada dentro de una ruta a crear. El orden se asigna segun la posicion dentro de la
// lista enviada (la primera es la inicial, la ultima es la final).
public class ParadaRequest {

    @NotBlank(message = "La ubicación de la parada es obligatoria")
    private String ubicacion;

    // Opcionales: si no se envian, la tarea programada de geocodificacion las completa despues.
    private Double latitud;
    private Double longitud;

    public ParadaRequest() {
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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
}
