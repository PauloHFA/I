package com.solid.isp.after.infrastructure;

import com.solid.isp.after.application.port.ExcluiPedido;
import com.solid.isp.after.application.port.ExportaPedidos;
import com.solid.isp.after.application.port.ListaPedidos;
import com.solid.isp.after.application.port.SalvaPedido;
import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.domain.Pedido;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação em memória para testes - implementa todos os papéis
 * Usada como "banco de dados" fake nos testes de integração
 */
public class InMemoryPedidoRepository implements SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos {
    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public void salvar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public Pedido buscarPorId(String id) {
        return pedidos.get(id);
    }

    @Override
    public List<Pedido> listar() {
        return new ArrayList<>(pedidos.values());
    }

    @Override
    public void excluir(String id) {
        pedidos.remove(id);
    }

    @Override
    public String exportarCsv() {
        List<Pedido> lista = listar();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Cliente,Data,Status,Total\n");
        for (Pedido p : lista) {
            sb.append(p.getId()).append(",")
              .append(p.getCliente().getNome()).append(",")
              .append(p.getData()).append(",")
              .append(p.getStatus()).append(",")
              .append(p.total()).append("\n");
        }
        return sb.toString();
    }

    // Utilitários para testes
    public void limpar() {
        pedidos.clear();
    }

    public int contar() {
        return pedidos.size();
    }
}