package br.com.coop.votacao.controller;

import br.com.coop.votacao.dto.request.AbrirSessaoRequest;
import br.com.coop.votacao.dto.response.SessaoResponse;
import br.com.coop.votacao.service.SessaoVotacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pautas/{pautaId}/sessoes")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoService;

    public SessaoVotacaoController(SessaoVotacaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping
    public ResponseEntity<SessaoResponse> abrirSessao(@PathVariable Long pautaId, @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        SessaoResponse response = sessaoService.abrir(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
