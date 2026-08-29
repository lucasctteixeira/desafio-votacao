package br.com.coop.votacao.controller;

import br.com.coop.votacao.dto.request.RegistrarVotoRequest;
import br.com.coop.votacao.dto.response.VotoResponse;
import br.com.coop.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pautas/{pautaId}")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/votos")
    public ResponseEntity<Void> registrarVoto(@PathVariable Long pautaId, @Valid @RequestBody RegistrarVotoRequest request) {
        votoService.registrar(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/resultado")
    public ResponseEntity<VotoResponse> contabilizar(@PathVariable Long pautaId) {
        VotoResponse resultado = votoService.contabilizar(pautaId);
        return ResponseEntity.ok(resultado);
    }
}