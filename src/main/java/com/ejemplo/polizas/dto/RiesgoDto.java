package com.ejemplo.polizas.dto;

import com.ejemplo.polizas.model.*;

public record RiesgoDto(Long id, String descripcion, EstadoRiesgo estado, Long polizaId) {
    public static RiesgoDto of(Riesgo r) {
        return new RiesgoDto(r.getId(), r.getDescripcion(), r.getEstado(), r.getPoliza().getId());
    }
}
