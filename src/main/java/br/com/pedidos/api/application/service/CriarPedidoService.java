package br.com.pedidos.api.application.service;

import br.com.pedidos.api.application.port.in.CriarPedido;
import br.com.pedidos.api.application.port.out.Pedidos;
import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;

import java.util.List;
import java.util.Objects;

public class CriarPedidoService implements CriarPedido {

    private final Pedidos pedidos;

    public CriarPedidoService(Pedidos pedidos) {
        this.pedidos = Objects.requireNonNull(pedidos, "O port de pedidos deve ser informado");
    }

    @Override
    public Pedido executar(String cliente, List<ItemPedido> itens) {
        Objects.requireNonNull(itens, "A lista de itens deve ser informada");
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve possuir itens");
        }

        var pedido = Pedido.novo(cliente, itens);
        return pedidos.salvar(pedido);
    }
}
