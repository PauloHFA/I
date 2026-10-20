# Interface Segregation Principle (ISP)

## Objetivo

Demonstrar que clientes não devem depender de métodos que não usam. O cenário usa o domínio de pedidos e compara uma interface de repositório ampla com portas definidas pelas necessidades de cada consumidor.

O princípio não exige uma interface por método. Interfaces devem ser coesas para seus clientes; consumidores que sempre usam operações em conjunto podem depender de uma interface composta.

## Cenário e dependências

| Cliente | `before`: `PedidoRepository` | `after`: papéis necessários |
| --- | --- | --- |
| `FinalizarPedido` | `salvar` | `SalvaPedido` |
| `ConsultarPedido` | `buscarPorId`, `listar` | `ConsultaPedidos` |
| `CancelarPedido` | `buscarPorId`, `excluir` | `BuscaPedido`, `ExcluiPedido` |
| `RelatorioFinanceiro` | `listar` | `ListaPedidos` |
| Réplica de leitura | É obrigada a expor leitura, escrita e exportação | `BuscaPedido` e `ListaPedidos` |

O `before` concentra cinco operações em `PedidoRepository`. A réplica precisa lançar exceções para escrita que não suporta, o fake de `FinalizarPedido` precisa implementar cinco métodos e o relatório pode acessar operações de escrita.

No `after`, as portas são definidas na camada de aplicação (`after/.../application/port`). Os casos de uso recebem apenas os papéis necessários. `ConsultaPedidos` compõe busca e listagem porque o cliente de consulta usa ambas. `GerenciaPedidos` compõe os papéis de gerenciamento. Uma implementação pode oferecer vários papéis sem obrigar seus clientes a depender de todos.

## Estrutura

```text
.
├── before/                  # Exemplo intencional da violação
├── after/                   # Portas segregadas e casos de uso
├── acceptance-test/         # Comparação de comportamento e arquitetura
├── docs/
│   └── isp.md
├── pom.xml                  # Agregador e configuração compartilhada
└── README.md
```

## Testes

Os testes de aceitação comparam o comportamento de `before` e `after` para finalizar pedidos, tratar pedidos sem itens, cancelar, consultar e gerar relatórios. Os testes de arquitetura verificam os tipos de dependência dos casos de uso e os papéis da réplica. Fakes pequenos exercitam `FinalizarPedido`, `CancelarPedido` e `RelatorioFinanceiro` sem implementar um repositório completo.

O contrato `ExportaPedidos` também foi ampliado com `exportarCabecalhoCsv()`. Somente implementações que oferecem exportação (`InMemoryPedidoRepository` e `MySqlPedidoRepository`) precisaram implementar a nova operação; os casos de uso e seus papéis permaneceram inalterados.

Execute todos os módulos a partir da raiz:

```bash
mvn clean verify
```

Para verificar somente a compilação:

```bash
mvn clean test-compile
```

Requisitos: JDK 21 e Maven 3.9 ou superior. No VS Code, abra a raiz do repositório e aguarde a importação automática dos projetos Maven.

## Métricas demonstradas

| Métrica | `before` | `after` |
| --- | --- | --- |
| Métodos necessários por `FinalizarPedido` | 1 de 5 | 1 de 1 |
| Métodos no fake de salvamento | 5 | 1 |
| Métodos de escrita expostos à réplica de leitura | 2 | 0 |
| Acesso do relatório a operações de escrita | Sim | Não |
| Casos de uso afetados por mudança em `exportarCsv()` | Todos os quatro | Nenhum; o papel exportador é independente |

## Relação com outros princípios

- **SRP** trata dos motivos para uma classe mudar; **ISP** trata das dependências impostas aos clientes.
- **LSP** é afetado quando uma implementação é forçada a lançar “não suportado”; segregar os papéis elimina essa obrigação estrutural.
- **DIP** é apoiado quando a aplicação define portas pequenas voltadas aos consumidores.

## Referências

- Robert C. Martin, “The Interface Segregation Principle”, *C++ Report*, 1996.
- Robert C. Martin, *Agile Software Development: Principles, Patterns, and Practices*.
- Martin Fowler, [Role Interface](https://martinfowler.com/bliki/RoleInterface.html).
- Plano de referência e convenções da série: [PauloHFA/S](https://github.com/PauloHFA/S).
