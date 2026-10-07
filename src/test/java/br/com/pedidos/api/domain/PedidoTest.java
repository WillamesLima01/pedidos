package br.com.pedidos.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class PedidoTest {

    @Test
    void deveCriarRascunhoAbertoVazioComTotalZero() {
        var pedido = Pedido.novo();

        assertTrue(pedido.id() != null);
        assertEquals(StatusPedido.ABERTO, pedido.status());
        assertTrue(pedido.itens().isEmpty());
        assertEquals(BigDecimal.ZERO, pedido.total());
    }

    @Test
    void deveCriarPedidosComIdsDiferentes() {
        assertNotEquals(Pedido.novo().id(), Pedido.novo().id());
    }

    @Test
    void deveSerUmRecord() {
        assertTrue(Pedido.class.isRecord());
    }

    @Test
    void deveAdicionarItemDevolvendoOutroPedidoEConservandoOriginal() {
        var pedido = Pedido.novo();
        var item = item("CAFE-500", 2, "18.90");

        var atualizado = pedido.adicionarItem(item);

        assertNotSame(pedido, atualizado);
        assertEquals(pedido.id(), atualizado.id());
        assertEquals(StatusPedido.ABERTO, pedido.status());
        assertTrue(pedido.itens().isEmpty());
        assertEquals(List.of(item), atualizado.itens());
    }

    @Test
    void deveCalcularTotalDoExemploDaAula() {
        var pedido = Pedido.novo()
                .adicionarItem(item("CAFE-500", 2, "18.90"));

        assertEquals(new BigDecimal("37.80"), pedido.total());
    }

    @Test
    void deveCalcularTotalComoSomaDosItens() {
        var pedido = Pedido.novo()
                .adicionarItem(item("CAFE-500", 2, "18.90"))
                .adicionarItem(item("PAO-001", 3, "2.50"));

        assertEquals(new BigDecimal("45.30"), pedido.total());
    }

    @Test
    void deveFazerCopiaDefensivaDosItens() {
        var itens = new ArrayList<ItemPedido>();
        itens.add(item("CAFE-500", 1, "18.90"));
        var pedido = new Pedido(java.util.UUID.randomUUID(), StatusPedido.ABERTO, itens);

        itens.clear();

        assertEquals(1, pedido.itens().size());
    }

    @Test
    void naoDevePermitirModificarColecaoExposta() {
        var pedido = Pedido.novo().adicionarItem(item("CAFE-500", 1, "18.90"));

        assertThrows(UnsupportedOperationException.class,
                () -> pedido.itens().add(item("PAO-001", 1, "2.50")));
    }

    @Test
    void devePermitirPagarPedidoAberto() {
        assertEquals(StatusPedido.PAGO, Pedido.novo().pagar().status());
    }

    @Test
    void devePermitirCancelarPedidoAberto() {
        assertEquals(StatusPedido.CANCELADO, Pedido.novo().cancelar().status());
    }

    @Test
    void deveRecusarOperacoesEmPedidoPago() {
        var pedido = Pedido.novo().pagar();

        assertThrows(IllegalStateException.class, pedido::pagar);
        assertThrows(IllegalStateException.class, pedido::cancelar);
        assertThrows(IllegalStateException.class,
                () -> pedido.adicionarItem(item("CAFE-500", 1, "18.90")));
    }

    @Test
    void deveRecusarOperacoesEmPedidoCancelado() {
        var pedido = Pedido.novo().cancelar();

        assertThrows(IllegalStateException.class, pedido::pagar);
        assertThrows(IllegalStateException.class, pedido::cancelar);
        assertThrows(IllegalStateException.class,
                () -> pedido.adicionarItem(item("CAFE-500", 1, "18.90")));
    }

    private static ItemPedido item(String codigo, int quantidade, String preco) {
        return new ItemPedido(codigo, quantidade, new BigDecimal(preco));
    }
}
