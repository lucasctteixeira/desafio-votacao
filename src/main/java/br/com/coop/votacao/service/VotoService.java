package br.com.coop.votacao.service;

import br.com.coop.votacao.cliente.CpfValidacao;
import br.com.coop.votacao.cliente.CpfValidacaoCliente;
import br.com.coop.votacao.cliente.StatusCpfEnum;
import br.com.coop.votacao.dto.request.RegistrarVotoRequest;
import br.com.coop.votacao.dto.response.VotoResponse;
import br.com.coop.votacao.exception.ConflitoException;
import br.com.coop.votacao.model.Pauta;
import br.com.coop.votacao.model.SessaoVotacao;
import br.com.coop.votacao.model.Voto;
import br.com.coop.votacao.model.enums.OpcaoVotoEnum;
import br.com.coop.votacao.repository.VotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VotoService {

    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoService;
    private final CpfValidacaoCliente cpfValidacaoCliente;

    public VotoService(VotoRepository votoRepository, PautaService pautaService, SessaoVotacaoService sessaoService, CpfValidacaoCliente cpfValidacaoCliente) {
        this.votoRepository = votoRepository;
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.cpfValidacaoCliente = cpfValidacaoCliente;
    }

    @Transactional
    public void registrar(Long pautaId, RegistrarVotoRequest request) {
        Pauta pauta = pautaService.buscarPorId(pautaId);

        SessaoVotacao sessao = sessaoService.buscarSessaoDaPauta(pautaId);
        if (!sessao.estaAberta()) {
            throw new ConflitoException("A sessão de votacao da pauta " + pautaId + " esta encerrada.");
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, request.associadoId())) {
            throw new ConflitoException("O associado " + request.associadoId() + " ja votou nesta pauta.");
        }

        StatusCpfEnum status = cpfValidacaoCliente.consultarCpf(request.cpf());
        if (status == StatusCpfEnum.UNABLE_TO_VOTE) {
            throw new ConflitoException("O associado do CPF: " + request.cpf() + " não esta apto a votar.");
        }

        Voto voto = new Voto(pauta, request.associadoId(), request.opcao());
        votoRepository.save(voto);
        log.info("Voto registrado: pautaId={}, associadoId={}, opcao={}",
                pautaId, request.associadoId(), request.opcao());
    }

    @Transactional(readOnly = true)
    public VotoResponse contabilizar(Long pautaId) {
        Pauta pauta = pautaService.buscarPorId(pautaId);
        long sim = votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVotoEnum.SIM);
        long nao = votoRepository.countByPautaIdAndOpcao(pautaId, OpcaoVotoEnum.NAO);
        long total = sim + nao;

        String resultado;
        if (sim > nao) {
            resultado = "APROVADA";
        } else if (nao > sim) {
            resultado = "REJEITADA";
        } else {
            resultado = "EMPATE";
        }

        log.info("Resultado contabilizado: pautaId={}, sim={}, nao={}, resultado={}",
                pautaId, sim, nao, resultado);

        return new VotoResponse(pautaId, pauta.getTitulo(), sim, nao, total, resultado);
    }
}
