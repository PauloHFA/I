import { Pedido } from '../domain/Pedido';
import { SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos } from '../application/ports';

/**
 * Implementação em memória para testes - implementa todos os papéis
 * Usada como "banco de dados" fake nos testes de integração
 */
export class InMemoryPedidoRepository
  implements SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos
{
  private readonly pedidos: Map<string, Pedido> = new Map();

  salvar(pedido: Pedido): void {
    this.pedidos.set(pedido.id, pedido);
  }

  buscarPorId(id: string): Pedido | null {
    return this.pedidos.get(id) ?? null;
  }

  listar(): Pedido[] {
    return Array.from(this.pedidos.values());
  }

  excluir(id: string): void {
    this.pedidos.delete(id);
  }

  exportarCsv(): string {
    const pedidos = this.listar();
    const header = 'ID,Cliente,Data,Status,Total\n';
    const rows = pedidos.map(p => 
      `${p.id},${p.cliente.nome},${p.data.toISOString()},${p.status},${p.total()}`
    ).join('\n');
    return header + rows;
  }

  // Utilitários para testes
  limpar(): void {
    this.pedidos.clear();
  }

  contar(): number {
    return this.pedidos.size;
  }
}