import { Pedido } from '../domain/Pedido';
import { SalvaPedido } from './ports';

/**
 * Caso de uso: Finalizar um pedido
 * Depende APENAS de SalvaPedido - nada mais
 */
export class FinalizarPedido {
  constructor(private readonly repo: SalvaPedido) {}

  executar(pedido: Pedido): void {
    pedido.finalizar();
    this.repo.salvar(pedido);
  }
}