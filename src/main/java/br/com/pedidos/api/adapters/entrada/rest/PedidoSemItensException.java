package br.com.pedidos.api.adapters.entrada.rest;

public class PedidoSemItensException extends RuntimeException {

    public PedidoSemItensException(String message, Throwable cause) {
        super(message, cause);
    }
}
