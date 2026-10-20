package com.solid.isp.after.application.usecase;

import com.solid.isp.after.application.port.ConsultaPedidos;
import com.solid.isp.after.domain.Pedido;
import java.util.List;

/**
 * Caso de uso: Consultar pedidos (tela de acompanhamento)
 * Depende de ConsultaPedidos (BuscaPedido + ListaPedidos) - métodos sempre usados juntos
 */
public class ConsultarPedido {
    private final ConsultaPedidos consulta;

    public ConsultarPedido(ConsultaPedidos consulta) {
        this.consulta = consulta;
    }

    public Pedido buscarPorId(String id) {
        return consulta.buscarPorId(id);
    }

    public List<Pedido> listarTodos() {
        return consulta.listar();
    }
}