package com.ejemplo.polizas.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CoreMockController {
    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    public record Evento(@NotNull String evento, @NotNull Long polizaId) {}

    @PostMapping("/core-mock/evento")
    public ResponseEntity<Void> evento(@Valid @RequestBody Evento evento) {
        log.info(
                "Intento de envío al CORE: evento={}, polizaId={}",
                evento.evento(),
                evento.polizaId());
        return ResponseEntity.accepted().build();
    }
}
