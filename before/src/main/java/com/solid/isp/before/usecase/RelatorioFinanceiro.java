package com.solid.isp.before.usecase;

import com.solid.isp.before.domain.Pedido;
import com.solid.isp.before.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Caso de uso: Relatório financeiro
 * PROBLEMA: Depende da interface GORDA - tem acesso a excluir(), salvar(), exportarCsv()
 * embora só precise de listar(). Risco de exclusão acidental!
 */
public class RelatorioFinanceiro {
    private final PedidoRepository repo;

    public RelatorioFinanceiro(PedidoRepository repo) {
        this.repo = repo;
    }

    public Relatorio gerar() {
        List<Pedido> lista = repo.listar();
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