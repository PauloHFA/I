package com.solid.isp.after.application.port;

import com.solid.isp.after.domain.Pedido;

/**
 * Interface composta: Gerenciamento de pedidos (Salva + Busca + Exclui)
 * Métodos sempre usados juntos pelos mesmos clientes
 */
public interface GerenciaPedidos extends SalvaPedido, BuscaPedido, ExcluiPedido {
    // Herda salvar(Pedido) de SalvaPedido
    // Herda buscarPorId(String) de BuscaPedido
    // Herda excluir(String) de ExcluiPedido
}