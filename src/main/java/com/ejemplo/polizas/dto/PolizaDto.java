package com.ejemplo.polizas.dto;

import com.ejemplo.polizas.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolizaDto(
        Long id,
        TipoPoliza tipo,
        EstadoPoliza estado,
        BigDecimal canonMensual,
        BigDecimal prima,
        LocalDate inicioVigencia,
        LocalDate finVigencia,
        int mesesVigencia) {
    public static PolizaDto of(Poliza p) {
        return new PolizaDto(
                p.getId(),
                p.getTipo(),
                p.getEstado(),
                p.getCanonMensual(),
                p.getPrima(),
                p.getInicioVigencia(),
                p.getFinVigencia(),
                p.getMesesVigencia());
    }
}
