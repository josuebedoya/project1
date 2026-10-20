package com.proyecto1.Security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

// Genera y valida los JWT que protegen la API principal del proyecto (independiente del JWT
// del Laboratorio 2, que usa su propia clave y su propia entidad Usuario).
@Component
public class JwtUtil {

    // Para HS512 la clave debe tener 64+ caracteres. Cambiala en application.properties/.env.
    @Value("${proyecto1.jwt.secret:cambia-esta-clave-del-proyecto1-por-una-muy-larga-de-minimo-64-caracteres-2026}")
    private String secret;

    private static final long EXPIRATION_MS = 2 * 60 * 60 * 1000; // 2 horas

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String login) {
        Date ahora = new Date();
        return Jwts.builder()
                .setSubject(login)
                .setIssuedAt(ahora)
                .setExpiration(new Date(ahora.getTime() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Devuelve el login (subject) si el token es valido; lanza JwtException/ExpiredJwtException si no.
    public String validarYObtenerLogin(String token) throws JwtException, ExpiredJwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
