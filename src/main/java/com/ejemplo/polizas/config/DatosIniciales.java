package com.ejemplo.polizas.config;

import com.ejemplo.polizas.model.*;
import com.ejemplo.polizas.repository.PolizaRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DatosIniciales {
    @Bean
    CommandLineRunner cargarDatos(PolizaRepository repository) {
        return args -> {
            Poliza individual =
                    new Poliza(
                            TipoPoliza.INDIVIDUAL,
                            new BigDecimal("1000000.00"),
                            LocalDate.of(2026, 1, 1),
                            12);
            individual.agregarRiesgo(new Riesgo("Apartamento 101"));
            Poliza colectiva =
                    new Poliza(
                            TipoPoliza.COLECTIVA,
                            new BigDecimal("2000000.00"),
                            LocalDate.of(2026, 1, 1),
                            12);
            colectiva.agregarRiesgo(new Riesgo("Local 201"));
            colectiva.agregarRiesgo(new Riesgo("Local 202"));
            repository.save(individual);
            repository.save(colectiva);
        };
    }
}
