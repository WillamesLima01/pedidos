package br.com.pedidos.api.adapter.out.persistence;

import br.com.pedidos.api.application.port.out.Pedidos;
import br.com.pedidos.api.domain.Pedido;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

@Repository
public class PedidosJpaAdapter implements Pedidos {

    private final PedidoSpringDataRepository repository;

    public PedidosJpaAdapter(PedidoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Pedido salvar(Pedido pedido) {
        var entidade = PedidoJpaMapper.paraEntidade(pedido);
        return PedidoJpaMapper.paraDominio(repository.save(entidade));
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(UUID id) {
        return repository.findById(id)
                .map(PedidoJpaMapper::paraDominio);
    }
}
