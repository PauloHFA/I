package com.solid.isp.after.application.port;

import com.solid.isp.after.domain.Pedido;

/**
 * Role interface: Salvar pedidos
 */
public interface SalvaPedido {
    void salvar(Pedido pedido);
}