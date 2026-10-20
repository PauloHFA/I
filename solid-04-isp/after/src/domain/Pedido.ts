export class Cliente {
  constructor(
    public readonly id: string,
    public readonly nome: string,
    public readonly email: string
  ) {}
}

export class Item {
  constructor(
    public readonly id: string,
    public readonly nome: string,
    public readonly preco: number,
    public readonly quantidade: number
  ) {}

  subtotal(): number {
    return this.preco * this.quantidade;
  }
}

export class Pedido {
  private readonly itens: Item[] = [];

  constructor(
    public readonly id: string,
    public readonly cliente: Cliente,
    public readonly data: Date = new Date(),
    public status: 'aberto' | 'finalizado' | 'cancelado' = 'aberto'
  ) {}

  adicionarItem(item: Item): void {
    this.itens.push(item);
  }

  getItens(): ReadonlyArray<Item> {
    return this.itens;
  }

  total(): number {
    return this.itens.reduce((sum, item) => sum + item.subtotal(), 0);
  }

  finalizar(): void {
    if (this.itens.length === 0) {
      throw new Error('Pedido sem itens não pode ser finalizado');
    }
    this.status = 'finalizado';
  }

  cancelar(): void {
    if (this.status === 'finalizado') {
      throw new Error('Pedido finalizado não pode ser cancelado');
    }
    this.status = 'cancelado';
  }
}