package com.ejemplo.polizas;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ejemplo.polizas.core.CoreGateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
class PolizaApiTest {
    @Autowired MockMvc mvc;
    @MockBean CoreGateway core;

    @Test
    void exigeClaveApi() throws Exception {
        mvc.perform(get("/polizas")).andExpect(status().isUnauthorized());
    }

    @Test
    void creaPolizaConLosDatosDeVigencia() throws Exception {
        mvc.perform(
                        post("/polizas")
                                .header("x-api-key", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "tipo": "INDIVIDUAL",
                                          "canonMensual": 1500000.00,
                                          "inicioVigencia": "2026-03-01",
                                          "mesesVigencia": 12,
                                          "riesgoInicial": "Apartamento 303"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.estado").value("VIGENTE"))
                .andExpect(jsonPath("$.prima").value(18000000.00))
                .andExpect(jsonPath("$.finVigencia").value("2027-02-28"));
    }

    @Test
    void validaLosDatosObligatoriosAlCrearPoliza() throws Exception {
        mvc.perform(
                        post("/polizas")
                                .header("x-api-key", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"tipo\":\"COLECTIVA\",\"mesesVigencia\":0}"))
                .andExpect(status().isBadRequest());

        mvc.perform(
                        post("/polizas")
                                .header("x-api-key", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"tipo":"INDIVIDUAL","canonMensual":1500000,"inicioVigencia":"2026-03-01","mesesVigencia":12}
                                        """))
                .andExpect(status().isBadRequest());

    }

    @Test
    void filtraYRenuevaConIpc() throws Exception {
        mvc.perform(get("/polizas").header("x-api-key", "123456").param("tipo", "COLECTIVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("COLECTIVA"));
        mvc.perform(
                        post("/polizas/1/renovar")
                                .header("x-api-key", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"ipcPorcentaje\":5.2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canonMensual").value(1052000.00))
                .andExpect(jsonPath("$.prima").value(12624000.00));
        verify(core).enviarActualizacion(1L);
    }

    @Test
    void validaTipoYCancelaRiesgos() throws Exception {
        mvc.perform(
                        post("/polizas/1/riesgos")
                                .header("x-api-key", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"descripcion\":\"otro\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/riesgos/1/cancelar").header("x-api-key", "123456"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/polizas/2/cancelar").header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
        mvc.perform(get("/polizas/2/riesgos").header("x-api-key", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("CANCELADO"))
                .andExpect(jsonPath("$[1].estado").value("CANCELADO"));
        mvc.perform(
                        post("/polizas/2/renovar")
                                .header("x-api-key", "123456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"ipcPorcentaje\":5}"))
                .andExpect(status().isBadRequest());
    }
}
