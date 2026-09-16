package com.ejemplo.polizas.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class HttpCoreGateway implements CoreGateway {
    private final RestClient client;

    public HttpCoreGateway(
            @Value("${app.core-url}") String baseUrl, @Value("${app.api-key}") String apiKey) {
        client = RestClient.builder().baseUrl(baseUrl).defaultHeader("x-api-key", apiKey).build();
    }

    @Override
    public void enviarActualizacion(Long polizaId) {
        client.post()
                .uri("/core-mock/evento")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("evento", "ACTUALIZACION", "polizaId", polizaId))
                .retrieve()
                .toBodilessEntity();
    }
}
