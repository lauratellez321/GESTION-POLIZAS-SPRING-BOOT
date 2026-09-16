package com.ejemplo.polizas.model;

import jakarta.persistence.*;

@Entity
public class Riesgo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado = EstadoRiesgo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Poliza poliza;

    protected Riesgo() {}

    public Riesgo(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public void setPoliza(Poliza poliza) {
        this.poliza = poliza;
    }

    public void cancelar() {
        estado = EstadoRiesgo.CANCELADO;
    }
}
