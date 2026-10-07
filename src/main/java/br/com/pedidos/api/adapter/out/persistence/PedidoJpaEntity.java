package br.com.pedidos.api.adapter.out.persistence;

import br.com.pedidos.api.domain.StatusPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedido")
public class PedidoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "cliente", nullable = false)
    private String cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemJpaEntity> itens = new ArrayList<>();

    protected PedidoJpaEntity() {
    }

    public PedidoJpaEntity(UUID id, String cliente, StatusPedido status) {
        this.id = id;
        this.cliente = cliente;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public List<ItemJpaEntity> getItens() {
        return itens;
    }

    public void adicionarItem(ItemJpaEntity item) {
        itens.add(item);
        item.associarAo(this);
    }
}
