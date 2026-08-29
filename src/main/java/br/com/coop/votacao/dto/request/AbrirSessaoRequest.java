package br.com.coop.votacao.dto.request;

import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(
        @Positive(message = "A duração de minutos deve ser positiva") Integer duracaoMinutos
) {
}
