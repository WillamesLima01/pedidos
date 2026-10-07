package br.com.pedidos.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemPedidoTest {

    @Test
    void deveCriarItemComDadosValidos() {
        var item = new ItemPedido("CAFE-500", 2, new BigDecimal("18.90"));

        assertEquals("CAFE-500", item.codigoProduto());
        assertEquals(2, item.quantidade());
        assertEquals(new BigDecimal("18.90"), item.precoUnitario());
    }

    @Test
    void deveRecusarCodigoNuloOuVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido(null, 1, new BigDecimal("1.00")));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("  ", 1, new BigDecimal("1.00")));
    }

    @Test
    void deveRecusarQuantidadeNaoPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", 0, new BigDecimal("1.00")));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", -1, new BigDecimal("1.00")));
    }

    @Test
    void deveRecusarPrecoNaoPositivo() {
        assertThrows(NullPointerException.class,
                () -> new ItemPedido("CAFE-500", 1, null));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", 1, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("CAFE-500", 1, new BigDecimal("-0.01")));
    }

    @Test
    void deveSerUmRecord() {
        assertEquals(true, ItemPedido.class.isRecord());
    }
}
