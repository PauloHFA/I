package com.solid.isp.after.application.port;

import com.solid.isp.after.domain.Pedido;
import java.util.List;

/**
 * Interface composta: Consulta de pedidos (Busca + Lista)
 * Métodos sempre usados juntos pelos mesmos clientes
 */
public interface ConsultaPedidos extends BuscaPedido, ListaPedidos {
    // Herda buscarPorId(String id) de BuscaPedido
    // Herda listar() de ListaPedidos
}