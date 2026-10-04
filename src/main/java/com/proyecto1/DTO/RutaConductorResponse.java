package com.proyecto1.DTO;

import com.proyecto1.Entities.Persona;

// Respuesta agrupada: un codigo de ruta y el conductor que la realizo, usada en la consulta
// de rutas por placa de vehiculo.
public record RutaConductorResponse(String codigoRuta, Persona conductor) {
}
