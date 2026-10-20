package com.solid.isp.before.usecase;

import com.solid.isp.before.domain.Pedido;
import com.solid.isp.before.repository.PedidoRepository;
import java.util.List;

/**
 * Caso de uso: Consultar pedidos
 * Depende da interface GORDA - só usa buscarPorId e listar, mas enxerga tudo
 */
public class ConsultarPedido {
    private final PedidoRepository repo;

    public ConsultarPedido(PedidoRepository repo) {
        this.repo = repo;
    }

    public Pedido buscarPorId(String id) {
        return repo.buscarPorId(id);
    }

    public List<Pedido> listarTodos() {
        return repo.listar();
    }
}