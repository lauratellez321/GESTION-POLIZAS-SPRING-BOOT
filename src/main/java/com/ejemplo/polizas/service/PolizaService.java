package com.ejemplo.polizas.service;

import com.ejemplo.polizas.core.CoreGateway;
import com.ejemplo.polizas.dto.*;
import com.ejemplo.polizas.exception.*;
import com.ejemplo.polizas.model.*;
import com.ejemplo.polizas.repository.*;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.time.LocalDate;
import java.util.List;

@Service
public class PolizaService {
    private final PolizaRepository polizas;
    private final RiesgoRepository riesgos;
    private final CoreGateway core;

    public PolizaService(PolizaRepository polizas, RiesgoRepository riesgos, CoreGateway core) {
        this.polizas = polizas;
        this.riesgos = riesgos;
        this.core = core;
    }

    @Transactional(readOnly = true)
    public List<PolizaDto> listar(TipoPoliza tipo, EstadoPoliza estado) {
        Specification<Poliza> filtro = (root, query, cb) -> cb.conjunction();
        if (tipo != null)
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        if (estado != null)
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        return polizas.findAll(filtro).stream().map(PolizaDto::of).toList();
    }

    @Transactional
    public PolizaDto crear(
            TipoPoliza tipo,
            BigDecimal canonMensual,
            LocalDate inicioVigencia,
            Integer mesesVigencia) {
        Poliza poliza = new Poliza(tipo, canonMensual, inicioVigencia, mesesVigencia);
        return PolizaDto.of(polizas.save(poliza));
    }

    @Transactional(readOnly = true)
    public List<RiesgoDto> listarRiesgos(Long polizaId) {
        return buscarPoliza(polizaId).getRiesgos().stream().map(RiesgoDto::of).toList();
    }

    @Transactional
    public PolizaDto renovar(Long id, BigDecimal ipcPorcentaje) {
        Poliza poliza = buscarPoliza(id);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA)
            throw new NegocioException("No se puede renovar una póliza cancelada");
        if (ipcPorcentaje == null
                || ipcPorcentaje.signum() < 0
                || ipcPorcentaje.compareTo(new BigDecimal("100")) > 0)
            throw new NegocioException("El IPC debe estar entre 0 y 100");
        BigDecimal factor = BigDecimal.ONE.add(ipcPorcentaje.movePointLeft(2));
        BigDecimal nuevoCanon =
                poliza.getCanonMensual().multiply(factor).setScale(2, RoundingMode.HALF_UP);
        core.enviarActualizacion(id);
        poliza.renovar(nuevoCanon);
        return PolizaDto.of(poliza);
    }

    @Transactional
    public PolizaDto cancelar(Long id) {
        Poliza poliza = buscarPoliza(id);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA)
            throw new NegocioException("La póliza ya está cancelada");
        core.enviarActualizacion(id);
        poliza.cancelar();
        return PolizaDto.of(poliza);
    }

    @Transactional
    public RiesgoDto agregarRiesgo(Long polizaId, String descripcion) {
        Poliza poliza = buscarPoliza(polizaId);
        if (poliza.getTipo() != TipoPoliza.COLECTIVA)
            throw new NegocioException("Solo se agregan riesgos a pólizas colectivas");
        if (poliza.getEstado() == EstadoPoliza.CANCELADA)
            throw new NegocioException("No se agregan riesgos a pólizas canceladas");
        if (descripcion == null || descripcion.isBlank())
            throw new NegocioException("La descripción es obligatoria");
        core.enviarActualizacion(polizaId);
        Riesgo riesgo = new Riesgo(descripcion.trim());
        poliza.agregarRiesgo(riesgo);
        return RiesgoDto.of(riesgos.save(riesgo));
    }

    @Transactional
    @SuppressWarnings("null")
    public RiesgoDto cancelarRiesgo(Long id) {
        Riesgo riesgo =
                riesgos.findById(id)
                        .orElseThrow(() -> new NoEncontradoException("Riesgo no encontrado"));
        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO)
            throw new NegocioException("El riesgo ya está cancelado");
        core.enviarActualizacion(riesgo.getPoliza().getId());
        riesgo.cancelar();
        return RiesgoDto.of(riesgo);
    }

    @SuppressWarnings("null")
    private Poliza buscarPoliza(Long id) {
        return polizas.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Póliza no encontrada"));
    }
}
