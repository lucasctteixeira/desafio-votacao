package br.com.coop.votacao.controller;

import br.com.coop.votacao.dto.response.VotoResponse;
import br.com.coop.votacao.service.VotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotoService votoService;

    @Test
    void deveRegistrarVotoERetornar201() throws Exception {
        String json = """
                {
                    "associadoId": "assoc-1",
                    "cpf": "52998224725",
                    "opcao": "SIM"
                }
                """;

        mockMvc.perform(post("/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar400QuandoCpfInvalido() throws Exception {
        String json = """
                {
                    "associadoId": "assoc-1",
                    "cpf": "11111111111",
                    "opcao": "SIM"
                }
                """;

        mockMvc.perform(post("/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveContabilizarERetornar200() throws Exception {
        VotoResponse response = new VotoResponse(1L, "Pauta", 3L, 1L, 4L, "APROVADA");
        when(votoService.contabilizar(eq(1L))).thenReturn(response);

        mockMvc.perform(get("/pautas/1/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("APROVADA"));
    }
}