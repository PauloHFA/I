package com.solid.isp.before.repository;

import com.solid.isp.before.domain.Pedido;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Réplica de leitura - OBRIGADA a implementar a interface gorda
 * Viola o LSP: lança exceção para métodos que não suporta
 * Viola o ISP: depende de métodos que não usa
 */
public class PedidoRepositoryReplica implements PedidoRepository {
    private final Map<String, Pedido> pedidos = new HashMap<>();

    public void sincronizar(List<Pedido> pedidosParaSincronizar) {
        pedidos.clear();
        for (Pedido pedido : pedidosParaSincronizar) {
            pedidos.put(pedido.getId(), pedido);
        }
        System.out.println("[Réplica-Before] Sincronizada com " + pedidos.size() + " pedidos");
    }

    // MÉTODOS DE LEITURA - funcionam normalmente
    @Override
    public Pedido buscarPorId(String id) {
        return pedidos.get(id);
    }

    @Override
    public List<Pedido> listar() {
        return new ArrayList<>(pedidos.values());
    }

    // MÉTODOS DE ESCRITA - VIOLAÇÃO DO LSP: lança exceção
    @Override
    public void salvar(Pedido pedido) {
        throw new UnsupportedOperationException("Réplica é somente leitura - não pode salvar");
    }

    @Override
    public void excluir(String id) {
        throw new UnsupportedOperationException("Réplica é somente leitura - não pode excluir");
    }

    // MÉTODO DE EXPORTAÇÃO - funciona, mas a réplica não deveria precisar implementar
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
}