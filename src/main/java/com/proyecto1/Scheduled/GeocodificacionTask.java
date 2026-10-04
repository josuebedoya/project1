package com.proyecto1.Scheduled;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto1.Entities.Trayecto;
import com.proyecto1.Repository.TrayectoRepository;

// Cada 90 segundos: busca trayectos sin latitud/longitud y las obtiene a partir de la
// ubicacion registrada usando la API externa de Google Maps Geocoding.
// Si GOOGLE_MAPS_API_KEY no esta configurada, la tarea se omite sin fallar.
@Component
public class GeocodificacionTask {

    @Autowired
    @Qualifier("ITrayectoRepo")
    private TrayectoRepository trayectoRepository;

    // Instancia propia: el starter webmvc de este proyecto no registra un bean ObjectMapper.
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${google.maps.api.key:}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final Logger logger = LogManager.getLogger(GeocodificacionTask.class);
    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

    @Scheduled(fixedRate = 90000)
    public void geocodificarTrayectosPendientes() {
        if (apiKey == null || apiKey.isBlank()) {
            logger.debug("GOOGLE_MAPS_API_KEY no configurada; se omite la geocodificación de trayectos");
            return;
        }

        List<Trayecto> pendientes = trayectoRepository.findByLatitudIsNullOrLongitudIsNull();
        for (Trayecto trayecto : pendientes) {
            try {
                geocodificar(trayecto);
            } catch (Exception e) {
                logger.error("ERROR GEOCODIFICAR_TRAYECTO: NO SE PUDO OBTENER COORDENADAS PARA EL TRAYECTO {}",
                        trayecto.getId(), e);
            }
        }
    }

    private void geocodificar(Trayecto trayecto) throws Exception {
        String direccion = URLEncoder.encode(trayecto.getUbicacion(), StandardCharsets.UTF_8);
        String url = String.format(GEOCODE_URL, direccion, apiKey);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode resultados = root.path("results");
        if (!"OK".equals(root.path("status").asText()) || !resultados.isArray() || resultados.isEmpty()) {
            logger.warn("GEOCODIFICACION SIN RESULTADOS PARA TRAYECTO {} ({}): {}", trayecto.getId(),
                    trayecto.getUbicacion(), root.path("status").asText());
            return;
        }

        JsonNode location = resultados.get(0).path("geometry").path("location");
        trayecto.setLatitud(location.path("lat").asDouble());
        trayecto.setLongitud(location.path("lng").asDouble());
        trayectoRepository.save(trayecto);
    }
}
