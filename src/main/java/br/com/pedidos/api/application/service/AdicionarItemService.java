package br.com.pedidos.api.application.service;

import br.com.pedidos.api.application.exception.PedidoFechadoException;
import br.com.pedidos.api.application.exception.PedidoNaoEncontradoException;
import br.com.pedidos.api.application.port.in.AdicionarItem;
import br.com.pedidos.api.application.port.out.Pedidos;
import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;

import java.util.Objects;
import java.util.UUID;

public class AdicionarItemService implements AdicionarItem {

    private final Pedidos pedidos;

    public AdicionarItemService(Pedidos pedidos) {
        this.pedidos = Objects.requireNonNull(pedidos, "O port de pedidos deve ser informado");
    }

    @Override
    public Pedido executar(UUID pedidoId, ItemPedido item) {
        var pedido = pedidos.buscarPorId(pedidoId)
                .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));
        try {
            return pedidos.salvar(pedido.adicionarItem(item));
        } catch (IllegalStateException exception) {
            throw new PedidoFechadoException("O pedido não aceita novos itens", exception);
        }
    }
}
