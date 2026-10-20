package com.proyecto1.Entities;

// Estado de un registro de cargue masivo de trayectos (Trayecto_tmp):
// CARGADO (recien insertado desde Excel), VALIDADO (paso las reglas de negocio),
// PROCESADO (ya se traslado a Trayecto), ERROR (incumple alguna regla).
public enum EstadoTrayectoTmp {
    CARGADO,
    VALIDADO,
    PROCESADO,
    ERROR
}
