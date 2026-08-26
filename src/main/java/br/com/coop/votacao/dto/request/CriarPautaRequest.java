package br.com.coop.votacao.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CriarPautaRequest(
        @NotBlank(message = "titulo e obrigatorio") String titulo,
        String descricao) {

}
