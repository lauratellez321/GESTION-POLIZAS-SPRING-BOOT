package com.ejemplo.polizas.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal prima;

    @Column(nullable = false)
    private LocalDate inicioVigencia;

    @Column(nullable = false)
    private LocalDate finVigencia;

    @Column(nullable = false)
    private Integer mesesVigencia;

    @Version private Long version;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {}

    public Poliza(
            TipoPoliza tipo, BigDecimal canonMensual, LocalDate inicioVigencia, int mesesVigencia) {
        if (mesesVigencia < 1) throw new IllegalArgumentException("La vigencia debe ser positiva");
        this.tipo = tipo;
        this.estado = EstadoPoliza.VIGENTE;
        this.canonMensual = canonMensual;
        this.mesesVigencia = mesesVigencia;
        this.inicioVigencia = inicioVigencia;
        this.finVigencia = inicioVigencia.plusMonths(mesesVigencia).minusDays(1);
        this.prima = canonMensual.multiply(BigDecimal.valueOf(mesesVigencia));
    }

    public Long getId() {
        return id;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public LocalDate getInicioVigencia() {
        return inicioVigencia;
    }

    public LocalDate getFinVigencia() {
        return finVigencia;
    }

    public Integer getMesesVigencia() {
        return mesesVigencia;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }

    public void agregarRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }

    public void renovar(BigDecimal nuevoCanon) {
        canonMensual = nuevoCanon;
        prima = nuevoCanon.multiply(BigDecimal.valueOf(mesesVigencia));
        inicioVigencia = finVigencia.plusDays(1);
        finVigencia = inicioVigencia.plusMonths(mesesVigencia).minusDays(1);
        estado = EstadoPoliza.RENOVADA;
    }

    public void cancelar() {
        estado = EstadoPoliza.CANCELADA;
        for (Riesgo riesgo : riesgos) riesgo.cancelar();
    }
}
