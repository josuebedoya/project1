package com.proyecto1.DTO;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Body para crear una ruta completa: una parada inicial, hasta 5 intermedias y una final,
// todas asociadas al mismo conductor, vehiculo y codigo de ruta.
public class TrayectoRutaRequest {

    @NotNull(message = "El id de la persona (conductor) es obligatorio")
    private Long personaId;

    @NotNull(message = "El id del vehículo es obligatorio")
    private Long vehiculoId;

    @NotBlank(message = "El código de ruta es obligatorio")
    private String codigoRuta;

    @NotBlank(message = "El login de usuario que registra el trayecto es obligatorio")
    private String loginUsuario;

    @NotEmpty(message = "La ruta debe tener como mínimo una parada inicial y una final")
    @Valid
    private List<ParadaRequest> paradas;

    public TrayectoRutaRequest() {
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public Long getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Long vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public List<ParadaRequest> getParadas() {
        return paradas;
    }

    public void setParadas(List<ParadaRequest> paradas) {
        this.paradas = paradas;
    }
}
