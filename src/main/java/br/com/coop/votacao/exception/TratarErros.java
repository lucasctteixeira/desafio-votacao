package br.com.coop.votacao.exception;

import br.com.coop.votacao.dto.response.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratarErros {

    private static final Logger log = LoggerFactory.getLogger(TratarErros.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratar404(RecursoNaoEncontradoException ex) {
        log.warn("Recurso nao encontrado: {}", ex.getMessage());
        return montar(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> tratarConflito(ConflitoException ex) {
        log.warn("Conflito: {}", ex.getMessage());
        return montar(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratar400(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Falha de validacao: {}", erros);
        return montar(HttpStatus.BAD_REQUEST, erros);
    }

    private ResponseEntity<ErroResponse> montar(HttpStatus status, String mensagem) {
        ErroResponse corpo = new ErroResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem
        );
        return ResponseEntity.status(status).body(corpo);
    }
}
