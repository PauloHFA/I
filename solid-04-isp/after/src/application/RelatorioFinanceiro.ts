import { Pedido } from '../domain/Pedido';
import { ListaPedidos } from './ports';

/**
 * Caso de uso: Relatório financeiro
 * Depende APENAS de ListaPedidos - somente leitura, sem acesso a escrita/exclusão
 */
export class RelatorioFinanceiro {
  constructor(private readonly pedidos: ListaPedidos) {}

  gerar(): { totalPedidos: number; valorTotal: number; pedidos: Pedido[] } {
    const lista = this.pedidos.listar();
    return {
      totalPedidos: lista.length,
      valorTotal: lista.reduce((sum, p) => sum + p.total(), 0),
      pedidos: lista
    };
  }
}