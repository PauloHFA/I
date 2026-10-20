package com.solid.isp.before.usecase;

import com.solid.isp.before.domain.Pedido;
import com.solid.isp.before.repository.PedidoRepository;

/**
 * Caso de uso: Cancelar um pedido
 * Depende da interface GORDA - tem acesso a excluir, mas também enxerga salvar, exportarCsv, etc.
 */
public class CancelarPedido {
    private final PedidoRepository repo;

    public CancelarPedido(PedidoRepository repo) {
        this.repo = repo;
    }

    public void executar(String id) {
        Pedido pedido = repo.buscarPorId(id);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido " + id + " não encontrado");
        }
        pedido.cancelar();
        repo.excluir(id);
    }
}