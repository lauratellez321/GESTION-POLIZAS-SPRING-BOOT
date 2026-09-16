package com.ejemplo.polizas.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RenovarRequest(
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal ipcPorcentaje) {}
