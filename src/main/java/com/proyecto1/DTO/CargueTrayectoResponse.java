package com.proyecto1.DTO;

import java.util.List;

import com.proyecto1.Entities.TrayectoTmp;

// Resumen del resultado de un cargue masivo de trayectos (Excel -> Trayecto_tmp -> Trayecto).
public record CargueTrayectoResponse(Long idCargue, int totalRegistros, int procesados, int conError,
        List<TrayectoTmp> detalle) {
}
