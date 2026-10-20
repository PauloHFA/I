import { Pedido } from '../domain/Pedido';

/**
 * Role interfaces (portas) - definidas pelo ponto de vista do cliente
 * Cada interface representa um "papel" que um cliente precisa
 */

export interface SalvaPedido {
  salvar(pedido: Pedido): void;
}

export interface BuscaPedido {
  buscarPorId(id: string): Pedido | null;
}

export interface ListaPedidos {
  listar(): Pedido[];
}

export interface ExcluiPedido {
  excluir(id: string): void;
}

export interface ExportaPedidos {
  exportarCsv(): string;
}

/**
 * Interfaces compostas para clientes que precisam de múltiplos papéis
 * Evita construtores com muitos parâmetros quando métodos são sempre usados juntos
 */
export interface ConsultaPedidos extends BuscaPedido, ListaPedidos {}

export interface GerenciaPedidos extends SalvaPedido, BuscaPedido, ExcluiPedido {}