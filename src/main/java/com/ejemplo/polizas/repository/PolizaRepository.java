package com.ejemplo.polizas.repository;

import com.ejemplo.polizas.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PolizaRepository
        extends JpaRepository<Poliza, Long>, JpaSpecificationExecutor<Poliza> {}
