package com.proyecto1.Exception;

// El recurso solicitado (vehiculo, documento, etc.) no existe.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
