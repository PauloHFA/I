package com.solid.isp.after.infrastructure;

import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.application.port.ListaPedidos;
import com.solid.isp.after.domain.Pedido;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Réplica de leitura - implementa APENAS interfaces de leitura
 * NÃO implementa SalvaPedido, ExcluiPedido, ExportaPedidos
 * Isso evita violação do LSP (não precisa lançar UnsupportedOperationException)
 */
public class PedidoRepositoryReplica implements BuscaPedido, ListaPedidos {
    private final Map<String, Pedido> pedidos = new HashMap<>();

    // Método interno para popular a réplica (simula replicação)
    public void sincronizar(List<Pedido> pedidosParaSincronizar) {
        pedidos.clear();
        for (Pedido pedido : pedidosParaSincronizar) {
            pedidos.put(pedido.getId(), pedido);
        }
        System.out.println("[Réplica] Sincronizada com " + pedidos.size() + " pedidos");
    }

    @Override
    public Pedido buscarPorId(String id) {
        Pedido pedido = pedidos.get(id);
        System.out.println("[Réplica] Busca por ID: " + id + " - " + (pedido != null ? "encontrado" : "não encontrado"));
        return pedido;
    }

    @Override
    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>(pedidos.values());
        System.out.println("[Réplica] Listando " + lista.size() + " pedidos");
        return lista;
    }

    // NÃO tem salvar(), excluir(), exportarCsv() - compilação falharia se tentasse usar
}