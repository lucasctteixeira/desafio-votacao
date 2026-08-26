package br.com.coop.votacao.dto.response;

import java.time.Instant;

public record PautaResponse(
        Long id,
        String titulo,
        String descricao,
        Instant criadaEm
) {
}
