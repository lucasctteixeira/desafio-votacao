package br.com.coop.votacao.integration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VotacaoIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarPautaEAbrirSessaoComSucesso() throws Exception {
        // cria uma pauta
        String pautaJson = """
                {
                    "titulo": "Pauta de integracao",
                    "descricao": "Teste completo"
                }
                """;

        mockMvc.perform(post("/pautas")
                        .contentType("application/json")
                        .content(pautaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Pauta de integracao"));
    }

    @Test
    void deveRetornar404AoAbrirSessaoEmPautaInexistente() throws Exception {
        mockMvc.perform(post("/pautas/9999/sessoes")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}