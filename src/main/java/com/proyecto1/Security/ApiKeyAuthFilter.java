package com.proyecto1.Security;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.proyecto1.Repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Filtro ligero de autenticacion: valida el header X-API-KEY contra Usuario.apikey.
// Se registra sobre los endpoints "administrativos" de cada recurso (ver SecurityFilterConfig);
// los servicios publicos (bajo "/publico/") quedan exentos mediante shouldNotFilter.
// Como solo las personas de tipo ADMINISTRATIVO tienen Usuario, validar el apikey ya garantiza
// que el token pertenece a un administrador.
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-API-KEY";

    private final UsuarioRepository usuarioRepository;

    public ApiKeyAuthFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getRequestURI().contains("/publico/")) {
            return true;
        }
        // Excepcion de arranque: crear una persona (y su Usuario, si es ADMINISTRATIVO) no puede
        // requerir un apikey, porque todavia no existe ninguno la primera vez que se usa el sistema.
        return "POST".equalsIgnoreCase(request.getMethod())
                && (request.getContextPath() + "/proyecto1/persona").equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER);
        if (apiKey == null || apiKey.isBlank() || usuarioRepository.findByApikey(apiKey).isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token inválido o ausente. Envíe el header " + HEADER + "\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
