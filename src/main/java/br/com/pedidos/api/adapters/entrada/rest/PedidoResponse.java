package br.com.pedidos.api.adapters.entrada.rest;

import br.com.pedidos.api.domain.Pedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        String clienteId,
        List<ItemResponse> itens,
        String status,
        BigDecimal total) {

    public static PedidoResponse de(Pedido pedido) {
        var itens = pedido.itens().stream()
                .map(item -> new ItemResponse(item.codigoProduto(), item.quantidade(), item.precoUnitario()))
                .toList();
        return new PedidoResponse(pedido.id(), pedido.cliente(), itens, pedido.status().name(), pedido.total());
    }

    public record ItemResponse(String sku, int quantidade, BigDecimal precoUnitario) {
    }
}
