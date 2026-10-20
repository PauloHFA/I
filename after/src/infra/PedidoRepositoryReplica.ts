import { Pedido } from '../domain/Pedido';
import { BuscaPedido, ListaPedidos } from '../application/ports';

/**
 * Réplica de leitura - implementa APENAS interfaces de leitura
 * NÃO implementa SalvaPedido, ExcluiPedido, ExportaPedidos
 * Isso evita violação do LSP (não precisa lançar NotImplementedException)
 */
export class PedidoRepositoryReplica implements BuscaPedido, ListaPedidos {
  private readonly pedidos: Map<string, Pedido> = new Map();

  // Método interno para popular a réplica (simula replicação)
  sincronizar(pedidos: Pedido[]): void {
    this.pedidos.clear();
    for (const pedido of pedidos) {
      this.pedidos.set(pedido.id, pedido);
    }
    console.log(`[Réplica] Sincronizada com ${pedidos.length} pedidos`);
  }

  buscarPorId(id: string): Pedido | null {
    const pedido = this.pedidos.get(id) ?? null;
    console.log(`[Réplica] Busca por ID: ${id} - ${pedido ? 'encontrado' : 'não encontrado'}`);
    return pedido;
  }

  listar(): Pedido[] {
    const lista = Array.from(this.pedidos.values());
    console.log(`[Réplica] Listando ${lista.length} pedidos`);
    return lista;
  }

  // NÃO tem salvar(), excluir(), exportarCsv() - compilação falharia se tentasse usar
}