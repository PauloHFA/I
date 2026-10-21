package com.solid.isp.after.infrastructure;

import com.solid.isp.after.application.port.ExcluiPedido;
import com.solid.isp.after.application.port.ExportaPedidos;
import com.solid.isp.after.application.port.ListaPedidos;
import com.solid.isp.after.application.port.SalvaPedido;
import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.application.port.ConsultaPedidos;
import com.solid.isp.after.application.port.GerenciaPedidos;
import com.solid.isp.after.domain.Pedido;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementação completa (MySQL) - implementa TODOS os papéis
 * Uma única classe pode implementar múltiplas interfaces
 */
public class MySqlPedidoRepository implements SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos, ConsultaPedidos, GerenciaPedidos {
    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public void salvar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
        System.out.println("[MySQL] Pedido " + pedido.getId() + " salvo");
    }

    @Override
    public Pedido buscarPorId(String id) {
        Pedido pedido = pedidos.get(id);
        System.out.println("[MySQL] Busca por ID: " + id + " - " + (pedido != null ? "encontrado" : "não encontrado"));
        return pedido;
    }

    @Override
    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>(pedidos.values());
        System.out.println("[MySQL] Listando " + lista.size() + " pedidos");
        return lista;
    }

    @Override
    public void excluir(String id) {
        boolean existia = pedidos.remove(id) != null;
        System.out.println("[MySQL] Exclusão do pedido " + id + ": " + (existia ? "sucesso" : "não encontrado"));
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
        System.out.println("[MySQL] Exportando " + lista.size() + " pedidos para CSV");
        return sb.toString();
    }

    @Override
    public String exportarCabecalhoCsv() {
        return "ID,Cliente,Data,Status,Total";
    }
}