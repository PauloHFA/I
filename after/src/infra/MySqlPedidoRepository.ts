import { Pedido } from '../domain/Pedido';
import { SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos } from '../application/ports';

/**
 * Implementação completa (MySQL) - implementa TODOS os papéis
 * Uma única classe pode implementar múltiplas interfaces
 */
export class MySqlPedidoRepository
  implements SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos
{
  private readonly pedidos: Map<string, Pedido> = new Map();

  // Simulação de persistência em memória para demonstração
  // Em produção, aqui haveria chamadas reais ao banco de dados

  salvar(pedido: Pedido): void {
    this.pedidos.set(pedido.id, pedido);
    console.log(`[MySQL] Pedido ${pedido.id} salvo`);
  }

  buscarPorId(id: string): Pedido | null {
    const pedido = this.pedidos.get(id) ?? null;
    console.log(`[MySQL] Busca por ID: ${id} - ${pedido ? 'encontrado' : 'não encontrado'}`);
    return pedido;
  }

  listar(): Pedido[] {
    const lista = Array.from(this.pedidos.values());
    console.log(`[MySQL] Listando ${lista.length} pedidos`);
    return lista;
  }

  excluir(id: string): void {
    const existia = this.pedidos.delete(id);
    console.log(`[MySQL] Exclusão do pedido ${id}: ${existia ? 'sucesso' : 'não encontrado'}`);
  }

  exportarCsv(): string {
    const pedidos = this.listar();
    const header = 'ID,Cliente,Data,Status,Total\n';
    const rows = pedidos.map(p => 
      `${p.id},${p.cliente.nome},${p.data.toISOString()},${p.status},${p.total()}`
    ).join('\n');
    console.log(`[MySQL] Exportando ${pedidos.length} pedidos para CSV`);
    return header + rows;
  }
}