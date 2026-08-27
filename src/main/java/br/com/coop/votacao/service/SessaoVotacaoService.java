package br.com.coop.votacao.service;

import br.com.coop.votacao.dto.request.AbrirSessaoRequest;
import br.com.coop.votacao.dto.response.SessaoResponse;
import br.com.coop.votacao.exception.ConflitoException;
import br.com.coop.votacao.model.Pauta;
import br.com.coop.votacao.model.SessaoVotacao;
import br.com.coop.votacao.repository.SessaoVotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;

@Service
public class SessaoVotacaoService {

    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoService.class);
    private static final int DURACAO_PADRAO_MINUTOS = 1;

    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaService pautaService;

    public SessaoVotacaoService(SessaoVotacaoRepository sessaoRepository,
                                PautaService pautaService) {
        this.sessaoRepository = sessaoRepository;
        this.pautaService = pautaService;
    }

    @Transactional
    public SessaoResponse abrir(Long pautaId, AbrirSessaoRequest request) {
        Pauta pauta = pautaService.buscarPorId(pautaId);

        if (sessaoRepository.existsByPautaId(pautaId)) {
            throw new ConflitoException("Ja existe uma sessao para a pauta " + pautaId);
        }

        int minutos = (request != null && request.duracaoMinutos() != null)
                ? request.duracaoMinutos()
                : DURACAO_PADRAO_MINUTOS;

        SessaoVotacao sessao = new SessaoVotacao(pauta, Duration.ofMinutes(minutos));
        sessao = sessaoRepository.save(sessao);
        log.info("Sessao aberta: pautaId={}, duracaoMin={}, fechaEm={}",
                pautaId, minutos, sessao.getFechaEm());

        return toResponse(sessao);
    }

    private SessaoResponse toResponse(SessaoVotacao s) {
        return new SessaoResponse(
                s.getId(),
                s.getPauta().getId(),
                s.getAbriuEm(),
                s.getFechaEm(),
                s.estaAberta()
        );
    }
}
