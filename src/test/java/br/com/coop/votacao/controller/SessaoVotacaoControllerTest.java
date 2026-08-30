package br.com.coop.votacao.controller;

import br.com.coop.votacao.dto.response.SessaoResponse;
import br.com.coop.votacao.exception.RecursoNaoEncontradoException;
import br.com.coop.votacao.service.SessaoVotacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessaoVotacaoController.class)
class SessaoVotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessaoVotacaoService sessaoService;

    @Test
    void deveAbrirSessaoERetornar201() throws Exception {
        SessaoResponse response = new SessaoResponse(1L, 1L, Instant.now(), Instant.now().plusSeconds(60), true);
        when(sessaoService.abrir(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/pautas/1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar404QuandoPautaNaoExiste() throws Exception {
        when(sessaoService.abrir(eq(9999L), any()))
                .thenThrow(new RecursoNaoEncontradoException("Pauta nao encontrada: 9999"));

        mockMvc.perform(post("/pautas/9999/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}