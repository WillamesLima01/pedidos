package br.com.pedidos.api.adapters.entrada.rest;

public class ItemInvalidoException extends RuntimeException {

    public ItemInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
