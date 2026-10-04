package com.proyecto1.DTO;

import com.proyecto1.Entities.TipoPersona;

// Respuesta publica: total de personas agrupadas por tipo.
public record PersonaPorTipoResponse(TipoPersona tipoPersona, Long total) {
}
