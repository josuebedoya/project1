package com.proyecto1.Exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }

    // Se dispara, por ejemplo, cuando el parametro "sort" de un listado paginado referencia una
    // propiedad que no existe en la entidad (incluido el placeholder ["string"] que deja Swagger
    // por defecto si no se borra antes de ejecutar).
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, String>> handlePropertyReference(PropertyReferenceException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "Parámetro de ordenamiento inválido: " + ex.getPropertyName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // JPA rechaza expresiones de "sort" que no sean una referencia de propiedad simple (incluye
    // el placeholder ["string"] que deja Swagger por defecto si no se borra antes de ejecutar).
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDataAccess(InvalidDataAccessApiUsageException ex) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("error", "Parámetro de ordenamiento (sort) inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
