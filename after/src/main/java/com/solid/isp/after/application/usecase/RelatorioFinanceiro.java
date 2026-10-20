package com.solid.isp.after.application.usecase;

import com.solid.isp.after.application.port.ListaPedidos;
import com.solid.isp.after.domain.Pedido;
import java.math.BigDecimal;
import java.util.List;

/**
 * Caso de uso: Relatório financeiro
 * Depende APENAS de ListaPedidos - somente leitura, sem acesso a escrita/exclusão
 */
public class RelatorioFinanceiro {
    private final ListaPedidos pedidos;

    public RelatorioFinanceiro(ListaPedidos pedidos) {
        this.pedidos = pedidos;
    }

    public Relatorio gerar() {
        List<Pedido> lista = pedidos.listar();
        BigDecimal valorTotal = lista.stream()
                .map(Pedido::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Relatorio(lista.size(), valorTotal, lista);
    }

    public static class Relatorio {
        private final int totalPedidos;
        private final BigDecimal valorTotal;
        private final List<Pedido> pedidos;

        public Relatorio(int totalPedidos, BigDecimal valorTotal, List<Pedido> pedidos) {
            this.totalPedidos = totalPedidos;
            this.valorTotal = valorTotal;
            this.pedidos = pedidos;
        }

        public int getTotalPedidos() { return totalPedidos; }
        public BigDecimal getValorTotal() { return valorTotal; }
        public List<Pedido> getPedidos() { return pedidos; }

        @Override
        public String toString() {
            return "Relatorio{totalPedidos=" + totalPedidos + ", valorTotal=" + valorTotal + "}";
        }
    }
}