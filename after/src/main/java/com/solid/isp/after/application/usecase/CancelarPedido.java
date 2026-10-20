package com.solid.isp.after.application.usecase;

import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.application.port.ExcluiPedido;
import com.solid.isp.after.domain.Pedido;

/**
 * Caso de uso: Cancelar um pedido
 * Depende de BuscaPedido e ExcluiPedido - apenas o que precisa
 */
public class CancelarPedido {
    private final BuscaPedido busca;
    private final ExcluiPedido exclui;

    public CancelarPedido(BuscaPedido busca, ExcluiPedido exclui) {
        this.busca = busca;
        this.exclui = exclui;
    }

    public void executar(String id) {
        Pedido pedido = busca.buscarPorId(id);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido " + id + " não encontrado");
        }
        pedido.cancelar();
        exclui.excluir(id);
    }
}