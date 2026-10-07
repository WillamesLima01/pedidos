package br.com.pedidos.api.adapters.entrada.rest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.pedidos.api.adapter.out.persistence.PedidosJpaAdapter;
import br.com.pedidos.api.domain.ItemPedido;
import br.com.pedidos.api.domain.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PedidosJpaAdapter pedidosJpaAdapter;

    @Test
    void deveCriarPedidoComStatus201ETotal3780() throws Exception {
        var resultado = mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("""
                                {
                                  "clienteId": "c-http",
                                  "itens": [{"sku": "CAFE-500", "quantidade": 2, "precoUnitario": 18.90}]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId", equalTo("c-http")))
                .andExpect(jsonPath("$.status", equalTo("ABERTO")))
                .andExpect(jsonPath("$.total", equalTo(37.80)))
                .andReturn();

        var id = UUID.fromString(JsonPathHelper.stringValue(resultado.getResponse().getContentAsString(), "id"));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pedido WHERE id = ?", Integer.class, id));
    }

    @Test
    void deveRecusarQuantidadeZeroCom422ESemPersistir() throws Exception {
        var antes = quantidadePedidos();

        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("""
                                {"clienteId":"c-zero","itens":[{"sku":"CAFE-500","quantidade":0,"precoUnitario":18.90}]}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", containsString("quantidade")));

        assertEquals(antes, quantidadePedidos());
    }

    @Test
    void deveRecusarItensVaziosCom422ESemPersistir() throws Exception {
        var antes = quantidadePedidos();

        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{\"clienteId\":\"c-vazio\",\"itens\":[]}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", containsString("itens")));

        assertEquals(antes, quantidadePedidos());
    }

    @Test
    void deveRecusarJsonMalformadoCom400ESemPersistir() throws Exception {
        var antes = quantidadePedidos();

        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content("{\"clienteId\":\"c-malformado\",\"itens\":["))
                .andExpect(status().isBadRequest());

        assertEquals(antes, quantidadePedidos());
    }

    @Test
    void deveAdicionarItemEAtualizarTotalComMesmoUuid() throws Exception {
        var pedido = pedidosJpaAdapter.salvar(Pedido.novo("c-add", List.of(
                new ItemPedido("CAFE-500", 2, new BigDecimal("18.90")))));

        mockMvc.perform(post("/pedidos/{id}/itens", pedido.id())
                        .contentType("application/json")
                        .content("{\"sku\":\"CAFE-500\",\"quantidade\":1,\"precoUnitario\":18.90}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(pedido.id().toString())))
                .andExpect(jsonPath("$.status", equalTo("ABERTO")))
                .andExpect(jsonPath("$.total", equalTo(56.70)));

        assertEquals(2, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM item_pedido WHERE pedido_id = ?", Integer.class, pedido.id()));
    }

    @Test
    void deveRetornar404ParaPedidoInexistenteESemGravar() throws Exception {
        var antes = quantidadeItens();

        mockMvc.perform(post("/pedidos/{id}/itens", UUID.randomUUID())
                        .contentType("application/json")
                        .content("{\"sku\":\"CAFE-500\",\"quantidade\":1,\"precoUnitario\":18.90}"))
                .andExpect(status().isNotFound());

        assertEquals(antes, quantidadeItens());
    }

    @Test
    void deveRetornar409ParaPedidoFechadoESemGravar() throws Exception {
        var pedido = pedidosJpaAdapter.salvar(Pedido.novo("c-closed", List.of(
                new ItemPedido("CAFE-500", 2, new BigDecimal("18.90"))))).pagar();
        pedidosJpaAdapter.salvar(pedido);
        var antes = quantidadeItens();

        mockMvc.perform(post("/pedidos/{id}/itens", pedido.id())
                        .contentType("application/json")
                        .content("{\"sku\":\"CAFE-500\",\"quantidade\":1,\"precoUnitario\":18.90}"))
                .andExpect(status().isConflict());

        assertEquals(antes, quantidadeItens());
    }

    @Test
    void deveRetornar422ParaQuantidadeInvalidaESemGravar() throws Exception {
        var pedido = pedidosJpaAdapter.salvar(Pedido.novo("c-invalid", List.of(
                new ItemPedido("CAFE-500", 2, new BigDecimal("18.90")))));
        var antes = quantidadeItens();

        mockMvc.perform(post("/pedidos/{id}/itens", pedido.id())
                        .contentType("application/json")
                        .content("{\"sku\":\"CAFE-500\",\"quantidade\":0,\"precoUnitario\":18.90}"))
                .andExpect(status().isUnprocessableEntity());

        assertEquals(antes, quantidadeItens());
    }

    private int quantidadePedidos() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pedido", Integer.class);
    }

    private int quantidadeItens() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM item_pedido", Integer.class);
    }

    private static final class JsonPathHelper {

        private JsonPathHelper() {
        }

        private static String stringValue(String json, String field) {
            var marker = "\"" + field + "\":\"";
            var start = json.indexOf(marker) + marker.length();
            var end = json.indexOf('"', start);
            return json.substring(start, end);
        }
    }
}
