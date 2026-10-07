package br.com.pedidos.api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.pedidos.api.application.exception.PedidoFechadoException;
import br.com.pedidos.api.application.exception.PedidoNaoEncontradoException;
import br.com.pedidos.api.application.port.out.Pedidos;
import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import br.com.pedidos.api.domain.StatusPedido;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class AdicionarItemServiceTest {

    @Test
    void deveBuscarAdicionarAtualizarTotalESalvar() {
        var original = Pedido.novo("c-1", List.of(item("CAFE-500", 2, "18.90")));
        var pedidos = new PedidosEmMemoria(original);
        var casoDeUso = new AdicionarItemService(pedidos);

        var salvo = casoDeUso.executar(original.id(), item("CAFE-500", 1, "18.90"));

        assertEquals(original.id(), salvo.id());
        assertEquals(new BigDecimal("56.70"), salvo.total());
        assertEquals(2, salvo.itens().size());
        assertSame(salvo, pedidos.ultimoSalvo);
        assertEquals(new BigDecimal("37.80"), original.total());
    }

    @Test
    void deveDevolverOPedidoRetornadoAoSalvar() {
        var original = Pedido.novo("c-1", List.of(item("CAFE-500", 2, "18.90")));
        var retorno = Pedido.novo("c-1", List.of(item("CAFE-500", 2, "18.90"), item("PAO-001", 1, "18.90")));
        var pedidos = new PedidosEmMemoria(original, retorno);

        var resultado = new AdicionarItemService(pedidos)
                .executar(original.id(), item("PAO-001", 1, "18.90"));

        assertSame(retorno, resultado);
    }

    @Test
    void deveRecusarPedidoInexistenteSemSalvar() {
        var pedidos = new PedidosEmMemoria(null);
        var casoDeUso = new AdicionarItemService(pedidos);

        assertThrows(PedidoNaoEncontradoException.class,
                () -> casoDeUso.executar(UUID.randomUUID(), item("CAFE-500", 1, "18.90")));
        assertEquals(0, pedidos.quantidadeSalvamentos);
    }

    @Test
    void deveRecusarPedidoFechadoSemSalvar() {
        var pago = Pedido.novo("c-1", List.of(item("CAFE-500", 2, "18.90"))).pagar();
        var pedidos = new PedidosEmMemoria(pago);

        assertThrows(PedidoFechadoException.class,
                () -> new AdicionarItemService(pedidos)
                        .executar(pago.id(), item("CAFE-500", 1, "18.90")));
        assertEquals(0, pedidos.quantidadeSalvamentos);
    }

    private static ItemPedido item(String sku, int quantidade, String preco) {
        return new ItemPedido(sku, quantidade, new BigDecimal(preco));
    }

    private static final class PedidosEmMemoria implements Pedidos {

        private final Pedido existente;
        private final Pedido retorno;
        private Pedido ultimoSalvo;
        private int quantidadeSalvamentos;

        private PedidosEmMemoria(Pedido existente) {
            this(existente, null);
        }

        private PedidosEmMemoria(Pedido existente, Pedido retorno) {
            this.existente = existente;
            this.retorno = retorno;
        }

        @Override
        public Pedido salvar(Pedido pedido) {
            quantidadeSalvamentos++;
            ultimoSalvo = pedido;
            return retorno == null ? pedido : retorno;
        }

        @Override
        public Optional<Pedido> buscarPorId(UUID id) {
            return existente != null && existente.id().equals(id)
                    ? Optional.of(existente)
                    : Optional.empty();
        }
    }
}
