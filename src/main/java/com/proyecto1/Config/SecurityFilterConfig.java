package com.proyecto1.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto1.Repository.UsuarioRepository;
import com.proyecto1.Security.ApiKeyAuthFilter;

@Configuration
public class SecurityFilterConfig {

    @Autowired
    @Qualifier("IUsuarioRepo")
    private UsuarioRepository usuarioRepository;

    // Protege los endpoints administrativos de cada recurso (Fase 1, Fase 2 y las consultas de
    // Fase 3). Los servicios publicos (bajo "/publico/") quedan exentos via shouldNotFilter.
    // La creacion de un trayecto (POST /proyecto1/trayecto) y el login/alta de persona no
    // requieren token, consistente con el resto del flujo de creacion de usuarios.
    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyAuthFilter() {
        FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ApiKeyAuthFilter(usuarioRepository));
        registration.addUrlPatterns(
                "/proyecto1/vehiculo", "/proyecto1/vehiculo/*",
                "/proyecto1/documento", "/proyecto1/documento/*",
                "/proyecto1/persona", "/proyecto1/persona/*",
                "/proyecto1/usuario", "/proyecto1/usuario/*",
                "/proyecto1/vehiculo-conductor", "/proyecto1/vehiculo-conductor/*",
                "/proyecto1/trayecto/consulta", "/proyecto1/trayecto/consulta/*");
        registration.setOrder(1);
        return registration;
    }
}
