package br.com.coop.votacao.dto.response;

import java.time.Instant;

public record ErroResponse(
        Instant horaDaRequisicao,
        int status,
        String erro,
        String mensagem
) {
}
