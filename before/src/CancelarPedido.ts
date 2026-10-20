import { Pedido } from './Pedido';
import { PedidoRepository } from './PedidoRepository';

/**
 * Caso de uso: Cancelar um pedido
 * Depende da interface GORDA - tem acesso a excluir, mas também enxerga salvar, exportarCsv, etc.
 */
export class CancelarPedido {
  constructor(private readonly repo: PedidoRepository) {}

  executar(id: string): void {
    const pedido = this.repo.buscarPorId(id);
    if (!pedido) {
      throw new Error(`Pedido ${id} não encontrado`);
    }
    pedido.cancelar();
    this.repo.excluir(id);
  }
}