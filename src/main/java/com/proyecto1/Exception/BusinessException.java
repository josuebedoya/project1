package com.proyecto1.Exception;

// Violacion de una regla de negocio (placa duplicada, formato invalido, falta un documento, etc.)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
