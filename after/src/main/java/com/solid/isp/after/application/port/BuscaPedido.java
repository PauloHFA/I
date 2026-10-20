package com.solid.isp.after.application.port;

import com.solid.isp.after.domain.Pedido;

/**
 * Role interface: Buscar pedidos por ID
 */
public interface BuscaPedido {
    Pedido buscarPorId(String id);
}