package com.ejemplo.polizas.dto;

import com.ejemplo.polizas.model.TipoPoliza;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearPolizaRequest(
        @NotNull TipoPoliza tipo,
        @NotNull @DecimalMin(value = "0.01") BigDecimal canonMensual,
        @NotNull LocalDate inicioVigencia,
        @NotNull @Min(1) Integer mesesVigencia,
        String riesgoInicial) {}
