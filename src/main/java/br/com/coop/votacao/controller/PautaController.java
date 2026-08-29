package br.com.coop.votacao.controller;

import br.com.coop.votacao.dto.request.CriarPautaRequest;
import br.com.coop.votacao.dto.response.PautaResponse;
import br.com.coop.votacao.repository.PautaRepository;
import br.com.coop.votacao.service.PautaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pautas")
public class PautaController {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping
    public ResponseEntity<PautaResponse> salvarPauta(@Valid @RequestBody CriarPautaRequest request) {
        PautaResponse response = pautaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
