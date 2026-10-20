package com.solid.isp.after.application.usecase;

import com.solid.isp.after.application.port.SalvaPedido;
import com.solid.isp.after.domain.Pedido;

/**
 * Caso de uso: Finalizar um pedido
 * Depende APENAS de SalvaPedido - nada mais
 */
public class FinalizarPedido {
    private final SalvaPedido repo;

    public FinalizarPedido(SalvaPedido repo) {
        this.repo = repo;
    }

    public void executar(Pedido pedido) {
        pedido.finalizar();
        repo.salvar(pedido);
    }
}