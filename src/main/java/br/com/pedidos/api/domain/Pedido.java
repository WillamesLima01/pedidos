package br.com.pedidos.api.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Pedido(UUID id, String cliente, StatusPedido status, List<ItemPedido> itens) {

    public Pedido {
        Objects.requireNonNull(id, "O identificador do pedido deve ser informado");
        Objects.requireNonNull(cliente, "O cliente deve ser informado");
        Objects.requireNonNull(status, "O status do pedido deve ser informado");
        itens = List.copyOf(Objects.requireNonNull(itens, "Os itens do pedido devem ser informados"));
    }

    public Pedido(UUID id, StatusPedido status, List<ItemPedido> itens) {
        this(id, "", status, itens);
    }

    public static Pedido novo() {
        return novo("", List.of());
    }

    public static Pedido novo(String cliente, List<ItemPedido> itens) {
        return new Pedido(UUID.randomUUID(), cliente, StatusPedido.ABERTO, itens);
    }

    public Pedido adicionarItem(ItemPedido item) {
        Objects.requireNonNull(item, "O item deve ser informado");
        exigirAberto();

        var novosItens = new ArrayList<>(itens);
        novosItens.add(item);
        return new Pedido(id, cliente, status, novosItens);
    }

    public Pedido pagar() {
        exigirAberto();
        return new Pedido(id, cliente, StatusPedido.PAGO, itens);
    }

    public Pedido cancelar() {
        exigirAberto();
        return new Pedido(id, cliente, StatusPedido.CANCELADO, itens);
    }

    public BigDecimal total() {
        return itens.stream()
                .map(item -> item.precoUnitario().multiply(BigDecimal.valueOf(item.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void exigirAberto() {
        if (status != StatusPedido.ABERTO) {
            throw new IllegalStateException("O pedido deve estar aberto");
        }
    }
}
