package br.com.coop.votacao.service;

import br.com.coop.votacao.dto.request.AbrirSessaoRequest;
import br.com.coop.votacao.dto.response.SessaoResponse;
import br.com.coop.votacao.exception.ConflitoException;
import br.com.coop.votacao.model.Pauta;
import br.com.coop.votacao.model.SessaoVotacao;
import br.com.coop.votacao.repository.SessaoVotacaoRepository;
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
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoVotacaoService sessaoService;

    @Test
    void deveAbrirSessaoComSucesso() {
        Long pautaId = 1L;
        Pauta pauta = new Pauta("Pauta teste", "Descrição");
        AbrirSessaoRequest request = new AbrirSessaoRequest(5);

        when(pautaService.buscarPorId(pautaId)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(false);
        when(sessaoRepository.save(any(SessaoVotacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessaoResponse response = sessaoService.abrir(pautaId, request);

        assertThat(response).isNotNull();
        assertThat(response.aberta()).isTrue();
    }

    @Test
    void deveLancarConflitoQuandoJaExisteSessao() {
        Long pautaId = 1L;
        Pauta pauta = new Pauta("Pauta teste", "Descrição");
        AbrirSessaoRequest request = new AbrirSessaoRequest(5);

        when(pautaService.buscarPorId(pautaId)).thenReturn(pauta);
        when(sessaoRepository.existsByPautaId(pautaId)).thenReturn(true);

        assertThatThrownBy(() -> sessaoService.abrir(pautaId, request))
                .isInstanceOf(ConflitoException.class)
                .hasMessageContaining("para a pauta");
    }

    @Test
    void deveLancarConflitoQuandoBuscarSessaoInexistente() {
        Long pautaId = 1L;
        when(sessaoRepository.findByPautaId(pautaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoService.buscarSessaoDaPauta(pautaId))
                .isInstanceOf(ConflitoException.class)
                .hasMessageContaining("para a pauta");
    }
}