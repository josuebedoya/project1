package com.proyecto1.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

// Carga variables desde un archivo ".env" en el directorio de trabajo (si existe) y las
// expone como property source de mayor precedencia, para poder referenciarlas en
// application.properties con ${VARIABLE}. Se ejecuta antes de que Spring resuelva esos
// placeholders, por lo que no requiere ninguna dependencia externa.
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = Path.of(".env");
        if (!Files.exists(envFile)) {
            return;
        }
        try {
            Map<String, Object> variables = new LinkedHashMap<>();
            List<String> lineas = Files.readAllLines(envFile);
            for (String linea : lineas) {
                String trim = linea.trim();
                if (trim.isEmpty() || trim.startsWith("#")) {
                    continue;
                }
                int separador = trim.indexOf('=');
                if (separador < 0) {
                    continue;
                }
                String clave = trim.substring(0, separador).trim();
                String valor = trim.substring(separador + 1).trim();
                if (valor.length() >= 2 && (valor.startsWith("\"") && valor.endsWith("\"")
                        || valor.startsWith("'") && valor.endsWith("'"))) {
                    valor = valor.substring(1, valor.length() - 1);
                }
                variables.put(clave, valor);
            }
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvFile", variables));
        } catch (IOException e) {
            System.err.println("ADVERTENCIA: no se pudo leer el archivo .env: " + e.getMessage());
        }
    }
}
