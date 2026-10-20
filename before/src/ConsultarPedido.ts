import { Pedido } from './Pedido';
import { PedidoRepository } from './PedidoRepository';

/**
 * Caso de uso: Consultar pedidos
 * Depende da interface GORDA - só usa buscarPorId e listar, mas enxerga tudo
 */
export class ConsultarPedido {
  constructor(private readonly repo: PedidoRepository) {}

  buscarPorId(id: string): Pedido | null {
    return this.repo.buscarPorId(id);
  }

  listarTodos(): Pedido[] {
    return this.repo.listar();
  }
}