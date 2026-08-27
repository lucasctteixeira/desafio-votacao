package br.com.coop.votacao.dto.response;

public record VotoResponse(Long pautaId,
                            String titulo,
                            long votosSim,
                            long votosNao,
                            long totalVotos,
                            String resultado) {
}
