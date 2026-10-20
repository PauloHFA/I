package com.solid.isp.before.repository;

import com.solid.isp.before.domain.Pedido;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação completa - implementa a interface gorda
 */
public class MySqlPedidoRepository implements PedidoRepository {
    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public void salvar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
        System.out.println("[MySQL-Before] Pedido " + pedido.getId() + " salvo");
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
        System.out.println("[MySQL-Before] Pedido " + id + " excluído");
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
        System.out.println("[MySQL-Before] Exportando " + lista.size() + " pedidos para CSV");
        return sb.toString();
    }
}