package br.com.pedidos.api.adapters.entrada.rest;

import br.com.pedidos.api.application.port.in.CriarPedido;
import br.com.pedidos.api.application.port.in.AdicionarItem;
import br.com.pedidos.api.domain.ItemPedido;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CriarPedido criarPedido;
    private final AdicionarItem adicionarItem;

    public PedidoController(CriarPedido criarPedido, AdicionarItem adicionarItem) {
        this.criarPedido = criarPedido;
        this.adicionarItem = adicionarItem;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        try {
            var itens = request.itens().stream()
                    .map(item -> new ItemPedido(item.sku(), item.quantidade(), item.precoUnitario()))
                    .toList();
            var pedido = criarPedido.executar(request.clienteId(), itens);
            var response = PedidoResponse.de(pedido);
            return ResponseEntity.created(URI.create("/pedidos/" + response.id())).body(response);
        } catch (IllegalArgumentException exception) {
            if (request.itens().isEmpty()) {
                throw new PedidoSemItensException(exception.getMessage(), exception);
            }
            throw new ItemInvalidoException(exception.getMessage(), exception);
        }
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<PedidoResponse> adicionarItem(
            @PathVariable UUID id,
            @Valid @RequestBody AdicionarItemRequest request) {
        try {
            var item = new ItemPedido(request.sku(), request.quantidade(), request.precoUnitario());
            var pedido = adicionarItem.executar(id, item);
            return ResponseEntity.ok(PedidoResponse.de(pedido));
        } catch (IllegalArgumentException exception) {
            throw new ItemInvalidoException(exception.getMessage(), exception);
        }
    }
}
