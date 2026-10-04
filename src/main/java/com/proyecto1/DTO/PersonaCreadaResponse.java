package com.proyecto1.DTO;

import com.proyecto1.Entities.Persona;

// Respuesta al crear una persona. Si es ADMINISTRATIVO trae las credenciales generadas.
// El password en texto plano solo se muestra esta vez; en la base de datos se guarda cifrado.
public record PersonaCreadaResponse(Persona persona, String login, String password, String apikey) {
}