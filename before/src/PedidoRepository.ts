import { Pedido } from '../domain/Pedido';

/**
 * VERSÃO QUE VIOLA O ISP - Interface "gorda"
 * Uma única interface com todos os métodos, forçando todos os clientes
 * e implementações a lidar com métodos irrelevantes
 */
export interface PedidoRepository {
  salvar(pedido: Pedido): void;
  buscarPorId(id: string): Pedido | null;
  listar(): Pedido[];
  excluir(id: string): void;
  exportarCsv(): string;
}