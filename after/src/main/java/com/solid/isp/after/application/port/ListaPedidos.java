package com.solid.isp.after.application.port;

import com.solid.isp.after.domain.Pedido;
import java.util.List;

/**
 * Role interface: Listar pedidos
 */
public interface ListaPedidos {
    List<Pedido> listar();
}