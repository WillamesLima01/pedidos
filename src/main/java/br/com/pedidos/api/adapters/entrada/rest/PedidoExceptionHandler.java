package br.com.pedidos.api.adapters.entrada.rest;

import br.com.pedidos.api.application.exception.PedidoFechadoException;
import br.com.pedidos.api.application.exception.PedidoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PedidoExceptionHandler {

    @ExceptionHandler(ItemInvalidoException.class)
    public ResponseEntity<Map<String, String>> itemInvalido(ItemInvalidoException exception) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(PedidoSemItensException.class)
    public ResponseEntity<Map<String, String>> pedidoSemItens(PedidoSemItensException exception) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(PedidoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> pedidoNaoEncontrado(PedidoNaoEncontradoException exception) {
        return resposta(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PedidoFechadoException.class)
    public ResponseEntity<Map<String, String>> pedidoFechado(PedidoFechadoException exception) {
        return resposta(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> requisicaoInvalida(Exception exception) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida");
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
