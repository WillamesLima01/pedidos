package br.com.pedidos.api.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import br.com.pedidos.api.domain.StatusPedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class PedidosJpaAdapterIT {

    @Autowired
    private PedidosJpaAdapter adapter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveSalvarERecuperarPedidoEmNovaTransacao() {
        var pedido = Pedido.novo("c-1", List.of(
                new ItemPedido("CAFE-500", 2, new BigDecimal("18.90"))));

        var salvo = adapter.salvar(pedido);
        var recuperado = adapter.buscarPorId(pedido.id()).orElseThrow();

        assertEquals(pedido.id(), salvo.id());
        assertEquals(pedido.id(), recuperado.id());
        assertEquals("c-1", recuperado.cliente());
        assertEquals(StatusPedido.ABERTO, recuperado.status());
        assertEquals(1, recuperado.itens().size());
        assertEquals("CAFE-500", recuperado.itens().getFirst().codigoProduto());
        assertEquals(2, recuperado.itens().getFirst().quantidade());
        assertEquals(0, new BigDecimal("18.90")
                .compareTo(recuperado.itens().getFirst().precoUnitario()));
        assertEquals(0, new BigDecimal("37.80").compareTo(recuperado.total()));

        var quantidadePersistida = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pedido WHERE id = ?", Integer.class, pedido.id());
        assertEquals(1, quantidadePersistida);
    }
}
