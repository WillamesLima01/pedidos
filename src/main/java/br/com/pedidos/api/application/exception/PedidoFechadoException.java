package br.com.pedidos.api.application.exception;

public class PedidoFechadoException extends RuntimeException {

    public PedidoFechadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
