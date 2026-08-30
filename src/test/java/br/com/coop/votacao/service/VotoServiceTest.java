package br.com.coop.votacao.service;

import br.com.coop.votacao.cliente.CpfValidacaoCliente;
import br.com.coop.votacao.cliente.StatusCpfEnum;
import br.com.coop.votacao.dto.request.RegistrarVotoRequest;
import br.com.coop.votacao.dto.response.VotoResponse;
import br.com.coop.votacao.exception.ConflitoException;
import br.com.coop.votacao.model.Pauta;
import br.com.coop.votacao.model.SessaoVotacao;
import br.com.coop.votacao.model.enums.OpcaoVotoEnum;
import br.com.coop.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoVotacaoService sessaoService;

    @Mock
    private CpfValidacaoCliente cpfValidacaoCliente;

    @Mock
    private SessaoVotacao sessao;

    @InjectMocks
    private VotoService votoService;

    private RegistrarVotoRequest request() {
        return new RegistrarVotoRequest("assoc-1", "52998224725", OpcaoVotoEnum.SIM);
    }

    @Test
    void deveRegistrarVotoComSucesso() {
        Long pautaId = 1L;
        when(pautaService.buscarPorId(pautaId)).thenReturn(new Pauta("Pauta", "Desc"));
        when(sessaoService.buscarSessaoDaPauta(pautaId)).thenReturn(sessao);
        when(sessao.estaAberta()).thenReturn(true);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, "assoc-1")).thenReturn(false);
        when(cpfValidacaoCliente.consultarCpf("52998224725")).thenReturn(StatusCpfEnum.ABLE_TO_VOTE);

        votoService.registrar(pautaId, request());

        verify(votoRepository).save(any());
    }

    @Test
    void deveLancarConflitoQuandoSessaoFechada() {
        Long pautaId = 1L;
        when(pautaService.buscarPorId(pautaId)).thenReturn(new Pauta("Pauta", "Desc"));
        when(sessaoService.buscarSessaoDaPauta(pautaId)).thenReturn(sessao);
        when(sessao.estaAberta()).thenReturn(false);

        assertThatThrownBy(() -> votoService.registrar(pautaId, request()))
                .isInstanceOf(ConflitoException.class)
                .hasMessageContaining("encerrada");

        verify(votoRepository, never()).save(any());
    }

    @Test
    void deveLancarConflitoQuandoAssociadoJaVotou() {
        Long pautaId = 1L;
        when(pautaService.buscarPorId(pautaId)).thenReturn(new Pauta("Pauta", "Desc"));
        when(sessaoService.buscarSessaoDaPauta(pautaId)).thenReturn(sessao);
        when(sessao.estaAberta()).thenReturn(true);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, "assoc-1")).thenReturn(true);

        assertThatThrownBy(() -> votoService.registrar(pautaId, request()))
                .isInstanceOf(ConflitoException.class)
                .hasMessageContaining("ja votou");

        verify(votoRepository, never()).save(any());
    }

    @Test
    void deveLancarConflitoQuandoCpfNaoApto() {
        Long pautaId = 1L;
        when(pautaService.buscarPorId(pautaId)).thenReturn(new Pauta("Pauta", "Desc"));
        when(sessaoService.buscarSessaoDaPauta(pautaId)).thenReturn(sessao);
        when(sessao.estaAberta()).thenReturn(true);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, "assoc-1")).thenReturn(false);
        when(cpfValidacaoCliente.consultarCpf("52998224725")).thenReturn(StatusCpfEnum.UNABLE_TO_VOTE);

        assertThatThrownBy(() -> votoService.registrar(pautaId, request()))
                .isInstanceOf(ConflitoException.class)
                .hasMessageContaining("apto");

        verify(votoRepository, never()).save(any());
    }

    @Test
    void deveContabilizarComoAprovada() {
        Long pautaId = 1L;
        when(pautaService.buscarPorId(pautaId)).thenReturn(new Pauta("Pauta", "Desc"));
        when(votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVotoEnum.SIM)).thenReturn(3L);
        when(votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVotoEnum.NAO)).thenReturn(1L);

        VotoResponse response = votoService.contabilizar(pautaId);

        assertThat(response.resultado()).isEqualTo("APROVADA");
        assertThat(response.totalVotos()).isEqualTo(4L);
    }

    @Test
    void deveContabilizarComoEmpate() {
        Long pautaId = 1L;
        when(pautaService.buscarPorId(pautaId)).thenReturn(new Pauta("Pauta", "Desc"));
        when(votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVotoEnum.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVotoEnum.NAO)).thenReturn(2L);

        VotoResponse response = votoService.contabilizar(pautaId);

        assertThat(response.resultado()).isEqualTo("EMPATE");
    }
}
