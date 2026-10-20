package com.solid.isp.before.usecase;

import com.solid.isp.before.domain.Pedido;
import com.solid.isp.before.repository.PedidoRepository;

/**
 * Caso de uso: Finalizar um pedido
 * Depende da interface GORDA - enxerga todos os métodos, embora só use salvar()
 */
public class FinalizarPedido {
    private final PedidoRepository repo;

    public FinalizarPedido(PedidoRepository repo) {
        this.repo = repo;
    }

    public void executar(Pedido pedido) {
        pedido.finalizar();
        repo.salvar(pedido);
    }
}