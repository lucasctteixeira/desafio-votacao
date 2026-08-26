package br.com.coop.votacao.service;

import br.com.coop.votacao.dto.request.CriarPautaRequest;
import br.com.coop.votacao.dto.response.PautaResponse;
import br.com.coop.votacao.model.Pauta;
import br.com.coop.votacao.repository.PautaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PautaService {

    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public PautaResponse criar(CriarPautaRequest request) {
        Pauta pauta = new Pauta(request.titulo(), request.descricao());
        log.info("Pauta criada: id={}, titulo='{}'", pauta.getId(), pauta.getTitulo());
        pauta = pautaRepository.save(pauta);

        return toResponse(pauta);
    }

//    @Transactional(readOnly = true)
//    public Pauta buscarPorId(Long id) {
//        return pautaRepository.findById(id)
//                .orElseThrow(() -> new RecursoNaoEncontradoException(
//                        "Pauta nao encontrada: " + id));
//    }

    private PautaResponse toResponse(Pauta pauta) {
        return new PautaResponse(
                pauta.getId(),
                pauta.getTitulo(),
                pauta.getDescricao(),
                pauta.getCriadaEm()
        );
    }
}
