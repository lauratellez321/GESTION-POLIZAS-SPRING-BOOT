package com.ejemplo.polizas.controller;

import com.ejemplo.polizas.dto.*;
import com.ejemplo.polizas.model.*;
import com.ejemplo.polizas.service.PolizaService;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PolizaController {
    private final PolizaService service;

    public PolizaController(PolizaService service) {
        this.service = service;
    }

    @PostMapping("/polizas")
    public ResponseEntity<PolizaDto> crear(@Valid @RequestBody CrearPolizaRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        service.crear(
                                body.tipo(),
                                body.canonMensual(),
                                body.inicioVigencia(),
                                body.mesesVigencia()));
    }

    @GetMapping("/polizas")
    public List<PolizaDto> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return service.listar(tipo, estado);
    }

    @GetMapping("/polizas/{id}/riesgos")
    public List<RiesgoDto> riesgos(@PathVariable Long id) {
        return service.listarRiesgos(id);
    }

    @PostMapping("/polizas/{id}/renovar")
    public PolizaDto renovar(@PathVariable Long id, @Valid @RequestBody RenovarRequest body) {
        return service.renovar(id, body.ipcPorcentaje());
    }

    @PostMapping("/polizas/{id}/cancelar")
    public PolizaDto cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PostMapping("/polizas/{id}/riesgos")
    public ResponseEntity<RiesgoDto> agregarRiesgo(
            @PathVariable Long id, @Valid @RequestBody CrearRiesgoRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.agregarRiesgo(id, body.descripcion()));
    }

    @PostMapping("/riesgos/{id}/cancelar")
    public RiesgoDto cancelarRiesgo(@PathVariable Long id) {
        return service.cancelarRiesgo(id);
    }
}
