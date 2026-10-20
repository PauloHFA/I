import { Pedido } from './Pedido';
import { PedidoRepository } from './PedidoRepository';

/**
 * Réplica de leitura - OBRIGADA a implementar a interface gorda
 * Viola o LSP: lança exceção para métodos que não suporta
 * Viola o ISP: depende de métodos que não usa
 */
export class PedidoRepositoryReplica implements PedidoRepository {
  private readonly pedidos: Map<string, Pedido> = new Map();

  sincronizar(pedidos: Pedido[]): void {
    this.pedidos.clear();
    for (const pedido of pedidos) {
      this.pedidos.set(pedido.id, pedido);
    }
    console.log(`[Réplica-Before] Sincronizada com ${pedidos.length} pedidos`);
  }

  // MÉTODOS DE LEITURA - funcionam normalmente
  buscarPorId(id: string): Pedido | null {
    return this.pedidos.get(id) ?? null;
  }

  listar(): Pedido[] {
    return Array.from(this.pedidos.values());
  }

  // MÉTODOS DE ESCRITA - VIOLAÇÃO DO LSP: lança exceção
  salvar(pedido: Pedido): void {
    throw new Error('Réplica é somente leitura - não pode salvar');
  }

  excluir(id: string): void {
    throw new Error('Réplica é somente leitura - não pode excluir');
  }

  // MÉTODO DE EXPORTAÇÃO - funciona, mas a réplica não deveria precisar implementar
  exportarCsv(): string {
    const pedidos = this.listar();
    const header = 'ID,Cliente,Data,Status,Total\n';
    const rows = pedidos.map(p => 
      `${p.id},${p.cliente.nome},${p.data.toISOString()},${p.status},${p.total()}`
    ).join('\n');
    return header + rows;
  }
}