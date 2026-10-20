package com.proyecto1.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto1.Security.JwtAuthFilter;
import com.proyecto1.Security.JwtUtil;

@Configuration
public class SecurityFilterConfig {

    @Autowired
    private JwtUtil jwtUtil;

    // Protege los endpoints administrativos de cada recurso (Fase 1, Fase 2, la creacion y
    // consultas de trayecto de Fase 3, y el cargue de Fase 4) exigiendo un JWT valido
    // (Authorization: Bearer <token>, ver JwtAuthFilter/JwtUtil). Los servicios publicos (bajo
    // "/publico/") quedan exentos via shouldNotFilter, al igual que el bootstrap (alta de la
    // primera persona y /authenticate, /login: todavia no existe ningun token la primera vez
    // que se usa el sistema).
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter() {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(jwtUtil));
        registration.addUrlPatterns(
                "/proyecto1/vehiculo", "/proyecto1/vehiculo/*",
                "/proyecto1/documento", "/proyecto1/documento/*",
                "/proyecto1/persona", "/proyecto1/persona/*",
                "/proyecto1/usuario", "/proyecto1/usuario/*",
                "/proyecto1/vehiculo-conductor", "/proyecto1/vehiculo-conductor/*",
                "/proyecto1/trayecto", "/proyecto1/trayecto/consulta", "/proyecto1/trayecto/consulta/*",
                "/proyecto1/trayecto/cargue", "/proyecto1/trayecto/cargue/*");
        registration.setOrder(1);
        return registration;
    }
}
