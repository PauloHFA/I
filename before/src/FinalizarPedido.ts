import { Pedido } from './Pedido';
import { PedidoRepository } from './PedidoRepository';

/**
 * Caso de uso: Finalizar um pedido
 * Depende da interface GORDA - enxerga todos os métodos, embora só use salvar()
 */
export class FinalizarPedido {
  constructor(private readonly repo: PedidoRepository) {}

  executar(pedido: Pedido): void {
    pedido.finalizar();
    this.repo.salvar(pedido);
  }
}