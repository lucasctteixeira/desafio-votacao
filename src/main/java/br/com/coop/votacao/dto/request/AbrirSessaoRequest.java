package br.com.coop.votacao.dto.request;

import jakarta.validation.constraints.Positive;

public record AbrirSessaoRequest(
        @Positive(message = "duracaoMinutos deve ser positiva") Integer duracaoMinutos
) {
}
