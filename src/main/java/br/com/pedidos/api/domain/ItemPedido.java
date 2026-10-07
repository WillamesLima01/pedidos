package br.com.pedidos.api.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record ItemPedido(String codigoProduto, int quantidade, BigDecimal precoUnitario) {

    public ItemPedido {
        if (codigoProduto == null || codigoProduto.isBlank()) {
            throw new IllegalArgumentException("O código do produto deve ser informado");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva");
        }
        Objects.requireNonNull(precoUnitario, "O preço unitário deve ser informado");
        if (precoUnitario.signum() <= 0) {
            throw new IllegalArgumentException("O preço unitário deve ser positivo");
        }
    }
}
