import { Pedido } from '../domain/Pedido';
import { BuscaPedido, ExcluiPedido } from './ports';

/**
 * Caso de uso: Cancelar um pedido
 * Depende de BuscaPedido e ExcluiPedido - apenas o que precisa
 */
export class CancelarPedido {
  constructor(
    private readonly busca: BuscaPedido,
    private readonly exclui: ExcluiPedido
  ) {}

  executar(id: string): void {
    const pedido = this.busca.buscarPorId(id);
    if (!pedido) {
      throw new Error(`Pedido ${id} não encontrado`);
    }
    pedido.cancelar();
    this.exclui.excluir(id);
  }
}