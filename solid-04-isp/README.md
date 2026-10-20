# SOLID #4 – Interface Segregation Principle (ISP)

> **Princípio da Segregação de Interfaces**  
> *"Os clientes não devem ser forçados a depender de interfaces que não utilizam."* — Robert C. Martin

## 🎯 Objetivo

Este repositório demonstra o **Interface Segregation Principle (ISP)** através de um sistema de pedidos implementado em **duas versões**:

| Versão | Descrição |
|--------|-----------|
| **`before/`** | Interface "gorda" (`PedidoRepository`) que viola o ISP |
| **`after/`** | Interfaces segregadas por papel (*role interfaces*) que respeitam o ISP |

---

## 📖 Conceito

O ISP nasceu de um problema real na Xerox: uma classe de impressora usada por vários tipos de tarefa tinha uma interface enorme. Qualquer mudança em um método forçava a recompilação e reteste de clientes que nem usavam aquele método.

**Ideia central**: A interface deve ser definida pelo ponto de vista do **cliente** ("o que eu preciso?"), não da implementação ("o que eu consigo fazer?"). Isso é chamado de **role interface** (interface de papel).

### O que o ISP NÃO é
- ❌ "Uma interface com um método" (isso é pulverização excessiva)
- ✅ Coesão em relação ao cliente, não o menor tamanho possível

---

## 🏗️ Estrutura do Projeto

```
solid-04-isp/
├── before/                    # Versão que VIOLA o ISP
│   ├── src/
│   │   ├── PedidoRepository.ts       # Interface gorda (5 métodos)
│   │   ├── MySqlPedidoRepository.ts  # Implementação completa
│   │   ├── PedidoRepositoryReplica.ts # Réplica - VIOLA LSP (lança exceções)
│   │   ├── FinalizarPedido.ts        # Cliente: só usa salvar()
│   │   ├── CancelarPedido.ts         # Cliente: usa buscar + excluir
│   │   ├── ConsultarPedido.ts        # Cliente: só usa buscar + listar
│   │   └── RelatorioFinanceiro.ts    # Cliente: só usa listar (mas tem acesso a tudo!)
│   └── tests/
│       └── isp-violations.test.ts    # Demonstra os problemas
│
├── after/                     # Versão que RESPEITA o ISP
│   ├── src/
│   │   ├── domain/
│   │   │   └── Pedido.ts             # Entidades: Pedido, Cliente, Item
│   │   ├── application/
│   │   │   ├── ports.ts              # Role interfaces (SalvaPedido, BuscaPedido, etc.)
│   │   │   ├── FinalizarPedido.ts    # Depende APENAS de SalvaPedido
│   │   │   ├── CancelarPedido.ts     # Depende de BuscaPedido + ExcluiPedido
│   │   │   ├── ConsultarPedido.ts    # Depende de ConsultaPedidos (Busca + Lista)
│   │   │   └── RelatorioFinanceiro.ts # Depende APENAS de ListaPedidos
│   │   └── infra/
│   │       ├── MySqlPedidoRepository.ts      # Implementa TODOS os papéis
│   │       ├── PedidoRepositoryReplica.ts    # Implementa APENAS leitura
│   │       └── InMemoryPedidoRepository.ts   # Para testes
│   └── tests/unit/
│       └── isp.test.ts               # Testes com fakes mínimos
│
├── docs/
│   └── isp.md                        # Este documento
│
├── README.md
└── CHANGELOG.md
```

---

## 🔴 Versão BEFORE – Problemas Demonstrados

### Interface Gorda
```typescript
interface PedidoRepository {
  salvar(pedido: Pedido): void;
  buscarPorId(id: string): Pedido | null;
  listar(): Pedido[];
  excluir(id: string): void;
  exportarCsv(): string;
}
```

### Problemas Reais

| Problema | Descrição |
|----------|-----------|
| **Fake gigante** | Testar `FinalizarPedido` exige implementar 5 métodos (só usa 1) |
| **Violação LSP** | `PedidoRepositoryReplica` lança `Error` em `salvar()` e `excluir()` |
| **Acesso indevido** | `RelatorioFinanceiro` pode chamar `excluir()` acidentalmente |
| **Acoplamento** | Mudança em `exportarCsv()` afeta TODOS os 6 clientes/implementações |
| **Nomes genéricos** | `PedidoRepository` não revela o papel de cada cliente |

---

## 🟢 Versão AFTER – Solução ISP

### Role Interfaces (Portas)
```typescript
interface SalvaPedido     { salvar(pedido: Pedido): void; }
interface BuscaPedido     { buscarPorId(id: string): Pedido | null; }
interface ListaPedidos    { listar(): Pedido[]; }
interface ExcluiPedido    { excluir(id: string): void; }
interface ExportaPedidos  { exportarCsv(): string; }

// Composição quando métodos são sempre usados juntos
interface ConsultaPedidos extends BuscaPedido, ListaPedidos {}
interface GerenciaPedidos extends SalvaPedido, BuscaPedido, ExcluiPedido {}
```

### Clientes Dependem Apenas do que Usam
```typescript
class FinalizarPedido {
  constructor(private readonly repo: SalvaPedido) {}  // Só salvar
}

class CancelarPedido {
  constructor(
    private readonly busca: BuscaPedido,
    private readonly exclui: ExcluiPedido
  ) {}  // Buscar + Excluir
}

class RelatorioFinanceiro {
  constructor(private readonly pedidos: ListaPedidos) {}  // Só leitura!
}
```

### Implementações
```typescript
// Completa: implementa todos os papéis
class MySqlPedidoRepository implements 
  SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos { }

// Réplica: APENAS leitura - compilação FALHA se tentar escrever
class PedidoRepositoryReplica implements BuscaPedido, ListaPedidos { }
```

---

## 🧪 Como Executar

### Pré-requisitos
- Node.js 18+
- TypeScript 5+

### Instalação
```bash
npm install
```

### Executar Testes - Versão AFTER (ISP Respeitado)
```bash
npx ts-node after/tests/unit/isp.test.ts
```

### Executar Testes - Versão BEFORE (Problemas)
```bash
npx ts-node before/tests/isp-violations.test.ts
```

---

## 📊 Comparativo de Métricas

| Métrica | Before (Viola ISP) | After (Respeita ISP) |
|---------|-------------------|---------------------|
| Métodos da interface usados por `FinalizarPedido` | 1 de 5 | 1 de 1 |
| Implementações com `throw NotImplemented` | 1+ | 0 |
| Métodos no fake de teste de `FinalizarPedido` | 5 | 1 |
| Clientes afetados ao mudar `exportarCsv` | **Todos (6)** | Apenas quem usa `ExportaPedidos` |
| `RelatorioFinanceiro` tem acesso a `excluir()` | ✅ Sim (perigoso) | ❌ Não (seguro) |
| Réplica de leitura compila com escrita | ✅ Sim (errado) | ❌ Erro de compilação |

---

## 🛠️ Técnicas Aplicadas

| Técnica | Uso no Projeto |
|---------|----------------|
| **Role Interfaces** | `SalvaPedido`, `ListaPedidos`, `ExportaPedidos` |
| **Extract Interface** | Dividiu `PedidoRepository` em 5 papéis |
| **Composição de Interfaces** | `ConsultaPedidos = BuscaPedido + ListaPedidos` |
| **Separação Leitura/Escrita** | Réplica implementa só `BuscaPedido + ListaPedidos` |
| **Adapter** | (Opcional) Para código legado que não pode mudar |

---

## ⚠️ Cuidados: O Outro Extremo

| Anti-padrão | Evitado Como |
|-------------|--------------|
| **Pulverização** | Agrupar métodos sempre usados juntos (`ConsultaPedidos`) |
| **Construtor inchado** | Se cliente precisa de 5 papéis → revisar SRP |
| **Segregação antecipada** | Dividir só quando há clientes reais com necessidades diferentes |
| **Interface por implementação** | Interfaces definidas pelo cliente, não pela classe |

---

## ✅ Checklist de Revisão

- [ ] Cada cliente depende apenas dos métodos que realmente usa?
- [ ] Nenhuma implementação lança `NotImplementedException`, tem método vazio ou retorna valor falso?
- [ ] As interfaces foram definidas a partir das necessidades dos clientes?
- [ ] Os nomes das interfaces revelam o papel (`SalvaPedido` vs `IRepository`)?
- [ ] Os fakes de teste são mínimos (1 método por papel)?
- [ ] Mudar um método afeta somente os clientes que o usam?
- [ ] Não houve pulverização excessiva?
- [ ] README explica o problema, a solução e como rodar?

---

## 📚 Referências

- Robert C. Martin — *The Interface Segregation Principle* (C++ Report, 1996)
- Robert C. Martin — *Agile Software Development: Principles, Patterns, and Practices*
- Robert C. Martin — *Clean Architecture*
- Martin Fowler — *RoleInterface* (bliki) e refatoração *Extract Interface*
- Gang of Four — *Design Patterns* (Adapter)

---

## 🔄 Próximo Passo

Com os 5 princípios SOLID implementados (`solid-01-srp` a `solid-05-dip`), o passo natural é criar um repositório integrado **`solid-all`** onde o sistema aplica os cinco princípios juntos.