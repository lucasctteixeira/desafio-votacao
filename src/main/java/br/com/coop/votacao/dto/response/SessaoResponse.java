package br.com.coop.votacao.dto.response;

import java.time.Instant;

public record SessaoResponse(
        Long id,
        Long pautaId,
        Instant abriuEm,
        Instant fechaEm,
        boolean aberta
) {
}
