package br.com.coop.votacao.controller;

import br.com.coop.votacao.dto.response.PautaResponse;
import br.com.coop.votacao.service.PautaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PautaService pautaService;

    @Test
    void deveCriarPautaERetornar201() throws Exception {
        PautaResponse response = new PautaResponse(1L, "Reforma", "Descricao", Instant.now());
        when(pautaService.criar(any())).thenReturn(response);

        String json = """
                {
                    "titulo": "Reforma",
                    "descricao": "Descricao"
                }
                """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Reforma"));
    }

    @Test
    void deveRetornar400QuandoTituloVazio() throws Exception {
        String json = """
                {
                    "titulo": "",
                    "descricao": "Descricao"
                }
                """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}