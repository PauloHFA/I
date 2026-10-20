import { Pedido } from './Pedido';
import { PedidoRepository } from './PedidoRepository';

/**
 * Implementação completa - implementa a interface gorda
 */
export class MySqlPedidoRepository implements PedidoRepository {
  private readonly pedidos: Map<string, Pedido> = new Map();

  salvar(pedido: Pedido): void {
    this.pedidos.set(pedido.id, pedido);
    console.log(`[MySQL-Before] Pedido ${pedido.id} salvo`);
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
}