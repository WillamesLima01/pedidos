package br.com.pedidos.api.adapters.entrada.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PedidoRequest(
        @NotBlank String clienteId,
        @NotNull @Valid List<ItemRequest> itens) {

    public record ItemRequest(
            @NotBlank String sku,
            @NotNull Integer quantidade,
            @NotNull BigDecimal precoUnitario) {
    }
}
