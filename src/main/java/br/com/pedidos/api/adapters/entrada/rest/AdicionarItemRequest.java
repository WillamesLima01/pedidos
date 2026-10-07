package br.com.pedidos.api.adapters.entrada.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AdicionarItemRequest(
        @NotBlank String sku,
        @NotNull Integer quantidade,
        @NotNull BigDecimal precoUnitario) {
}
