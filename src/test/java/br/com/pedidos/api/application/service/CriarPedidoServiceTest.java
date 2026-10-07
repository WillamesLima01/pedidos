package br.com.pedidos.api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class CriarPedidoServiceTest {

    @Test
    void deveCriarPedidoComClienteEItensESalvar() {
        var repositorio = new PedidosEmMemoria();
        var casoDeUso = new CriarPedidoService(repositorio);
        var itens = List.of(item("CAFE-500", 2, "18.90"));

        var pedido = casoDeUso.executar("Maria", itens);

        assertEquals("Maria", pedido.cliente());
        assertEquals(StatusPedido.ABERTO, pedido.status());
        assertEquals(itens, pedido.itens());
        assertSame(pedido, repositorio.ultimoPedido);
    }

    @Test
    void deveRecusarListaDeItensVaziaAntesDeSalvar() {
        var repositorio = new PedidosEmMemoria();
        var casoDeUso = new CriarPedidoService(repositorio);

        assertThrows(IllegalArgumentException.class,
                () -> casoDeUso.executar("Maria", List.of()));
        assertTrue(repositorio.ultimoPedido == null);
    }

    @Test
    void deveDevolverExatamenteOPedidoRetornadoPeloPortDeSaida() {
        var pedidoSalvo = Pedido.novo("Maria", List.of(item("CAFE-500", 1, "18.90")));
        var repositorio = new PedidosEmMemoria(pedidoSalvo);
        var casoDeUso = new CriarPedidoService(repositorio);

        var resultado = casoDeUso.executar("Maria", List.of(item("CAFE-500", 1, "18.90")));

        assertSame(pedidoSalvo, resultado);
    }

    @Test
    void deveAceitarImplementacaoEmMemoriaSemFramework() {
        var repositorio = new PedidosEmMemoria();
        var casoDeUso = new CriarPedidoService(repositorio);

        var resultado = casoDeUso.executar("Maria", List.of(item("CAFE-500", 1, "18.90")));

        assertEquals(new BigDecimal("18.90"), resultado.total());
    }

    private static ItemPedido item(String codigo, int quantidade, String preco) {
        return new ItemPedido(codigo, quantidade, new BigDecimal(preco));
    }

    private static final class PedidosEmMemoria implements Pedidos {

        private final Pedido retorno;
        private Pedido ultimoPedido;
        private final List<Pedido> pedidos = new ArrayList<>();

        private PedidosEmMemoria() {
            this(null);
        }

        private PedidosEmMemoria(Pedido retorno) {
            this.retorno = retorno;
        }

        @Override
        public Pedido salvar(Pedido pedido) {
            ultimoPedido = pedido;
            pedidos.add(pedido);
            return retorno == null ? pedido : retorno;
        }

        @Override
        public Optional<Pedido> buscarPorId(UUID id) {
            return Optional.empty();
        }
    }
}
