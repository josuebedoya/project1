package com.proyecto1.Security;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Autenticacion real de la API: exige el header "Authorization: Bearer <jwt>" valido (ver
// JwtUtil). Reemplaza al antiguo filtro de X-API-KEY. Se registra sobre los endpoints
// administrativos de cada recurso (ver SecurityFilterConfig); los servicios publicos (bajo
// "/publico/") y el bootstrap (alta de la primera persona, /authenticate, /login) quedan
// exentos via shouldNotFilter.
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getRequestURI().contains("/publico/")) {
            return true;
        }
        String metodo = request.getMethod();
        String uri = request.getRequestURI();
        String contexto = request.getContextPath();

        // Excepcion de arranque: crear una persona (y su Usuario, si es ADMINISTRATIVO) no puede
        // requerir un token, porque todavia no existe ninguno la primera vez que se usa el sistema.
        if ("POST".equalsIgnoreCase(metodo) && (contexto + "/proyecto1/persona").equals(uri)) {
            return true;
        }
        // Obtener el token (o el apikey, que se conserva como consulta aparte) tampoco puede
        // requerir el propio token.
        return "POST".equalsIgnoreCase(metodo)
                && ((contexto + "/proyecto1/usuario/authenticate").equals(uri)
                        || (contexto + "/proyecto1/usuario/login").equals(uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader(HEADER);

        if (header == null || !header.startsWith(PREFIX)) {
            responderNoAutorizado(response, "Token inválido o ausente. Envíe el header "
                    + HEADER + ": " + PREFIX + "<token>");
            return;
        }

        try {
            jwtUtil.validarYObtenerLogin(header.substring(PREFIX.length()));
            chain.doFilter(request, response);
        } catch (JwtException e) {
            responderNoAutorizado(response, "Token inválido o vencido");
        }
    }

    private void responderNoAutorizado(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }
}
