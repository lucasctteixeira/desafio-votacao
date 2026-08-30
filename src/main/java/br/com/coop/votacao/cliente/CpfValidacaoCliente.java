package br.com.coop.votacao.cliente;

import br.com.coop.votacao.exception.RecursoNaoEncontradoException;
import br.com.coop.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CpfValidacaoCliente implements CpfValidacao{

    private static final Logger log = LoggerFactory.getLogger(CpfValidacaoCliente.class);

    @Override
    public StatusCpfEnum consultarCpf(String cpf) {
        if (Math.random() < 0.5) {
            log.info("CPF {} considerado inválido pelo serviço externo", cpf);
            throw new RecursoNaoEncontradoException("CPF não encontrado: " + cpf);
        }

        StatusCpfEnum status = (Math.random() < 0.5) ? StatusCpfEnum.ABLE_TO_VOTE : StatusCpfEnum.UNABLE_TO_VOTE;

        log.info("CPF {} valido; status de votacao: {}", cpf, status);
        return status;
    }

}
