package br.com.pedidos.api.adapter.out.persistence;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;

import java.util.List;

final class PedidoJpaMapper {

    private PedidoJpaMapper() {
    }

    static PedidoJpaEntity paraEntidade(Pedido pedido) {
        var entidade = new PedidoJpaEntity(pedido.id(), pedido.cliente(), pedido.status());
        pedido.itens().stream()
                .map(item -> new ItemJpaEntity(item.codigoProduto(), item.quantidade(), item.precoUnitario()))
                .forEach(entidade::adicionarItem);
        return entidade;
    }

    static Pedido paraDominio(PedidoJpaEntity entidade) {
        var itens = entidade.getItens().stream()
                .map(item -> new ItemPedido(item.getSku(), item.getQuantidade(), item.getPrecoUnitario()))
                .toList();
        return new Pedido(entidade.getId(), entidade.getCliente(), entidade.getStatus(), itens);
    }
}
