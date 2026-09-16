package com.ejemplo.polizas.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearRiesgoRequest(@NotBlank String descripcion) {}
