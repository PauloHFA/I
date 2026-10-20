import { Pedido } from './Pedido';
import { PedidoRepository } from './PedidoRepository';

/**
 * Caso de uso: Relatório financeiro
 * PROBLEMA: Depende da interface GORDA - tem acesso a excluir(), salvar(), exportarCsv()
 * embora só precise de listar(). Risco de exclusão acidental!
 */
export class RelatorioFinanceiro {
  constructor(private readonly repo: PedidoRepository) {}

  gerar(): { totalPedidos: number; valorTotal: number; pedidos: Pedido[] } {
    const lista = this.repo.listar();
    return {
      totalPedidos: lista.length,
      valorTotal: lista.reduce((sum, p) => sum + p.total(), 0),
      pedidos: lista
    };
  }
}