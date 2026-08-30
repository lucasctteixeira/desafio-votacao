package br.com.coop.votacao.repository;

import br.com.coop.votacao.model.Voto;
import br.com.coop.votacao.model.enums.OpcaoVotoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndCpf(Long pautaId, String cpf);

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);

    long countByPautaIdAndOpcao(Long pautaId, OpcaoVotoEnum opcao);
}
