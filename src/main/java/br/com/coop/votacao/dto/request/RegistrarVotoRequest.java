package br.com.coop.votacao.dto.request;

import br.com.coop.votacao.model.enums.OpcaoVotoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarVotoRequest(@NotBlank(message = "O código do associado é obrigatório") String associadoId,
                                   @NotNull(message = " A opção é obrigatória (SIM ou NAO)") OpcaoVotoEnum opcao) {
}
