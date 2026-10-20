import { Pedido } from '../domain/Pedido';
import { ConsultaPedidos } from './ports';

/**
 * Caso de uso: Consultar pedidos (tela de acompanhamento)
 * Depende de ConsultaPedidos (BuscaPedido + ListaPedidos) - métodos sempre usados juntos
 */
export class ConsultarPedido {
  constructor(private readonly consulta: ConsultaPedidos) {}

  buscarPorId(id: string): Pedido | null {
    return this.consulta.buscarPorId(id);
  }

  listarTodos(): Pedido[] {
    return this.consulta.listar();
  }
}