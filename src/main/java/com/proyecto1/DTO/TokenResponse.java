package com.proyecto1.DTO;

// JWT como "Authorization: Bearer <token>".
public record TokenResponse(String login, String token) {
}
