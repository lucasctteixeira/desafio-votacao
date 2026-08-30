package br.com.coop.votacao.service;

import br.com.coop.votacao.dto.request.CriarPautaRequest;
import br.com.coop.votacao.dto.response.PautaResponse;
import br.com.coop.votacao.exception.RecursoNaoEncontradoException;
import br.com.coop.votacao.model.Pauta;
import br.com.coop.votacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PautaServiceTest {
    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    void deveCriarPautaComSucesso() {
        CriarPautaRequest request = new CriarPautaRequest("Reforma do estatuto", "Descricao");
        Pauta pautaSalva = new Pauta("Reforma do estatuto", "Descricao");
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaSalva);

        PautaResponse response = pautaService.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.titulo()).isEqualTo("Reforma do estatuto");
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }
}
