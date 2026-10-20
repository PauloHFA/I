# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.0.0] - 2026-10-20

### Adicionado
- **Versão BEFORE** - Demonstração da violação do ISP (Java 17 + Maven)
  - Interface gorda `PedidoRepository` com 5 métodos
  - Implementação completa `MySqlPedidoRepository`
  - Réplica de leitura `PedidoRepositoryReplica` que viola LSP (lança UnsupportedOperationException)
  - 4 casos de uso dependendo da interface gorda: `FinalizarPedido`, `CancelarPedido`, `ConsultarPedido`, `RelatorioFinanceiro`
  - Testes demonstrando os problemas: fake gigante, violação LSP, acesso indevido, acoplamento

- **Versão AFTER** - Implementação respeitando o ISP (Java 17 + Maven)
  - Entidades de domínio: `Pedido`, `Cliente`, `Item`
  - Role interfaces (portas): `SalvaPedido`, `BuscaPedido`, `ListaPedidos`, `ExcluiPedido`, `ExportaPedidos`
  - Interfaces compostas: `ConsultaPedidos`, `GerenciaPedidos`
  - Casos de uso com dependências mínimas:
    - `FinalizarPedido` → `SalvaPedido`
    - `CancelarPedido` → `BuscaPedido` + `ExcluiPedido`
    - `ConsultarPedido` → `ConsultaPedidos`
    - `RelatorioFinanceiro` → `ListaPedidos`
  - Implementações:
    - `MySqlPedidoRepository` - implementa todos os papéis
    - `PedidoRepositoryReplica` - implementa APENAS leitura (compilação falha se tentar escrita)
    - `InMemoryPedidoRepository` - para testes
  - Testes com fakes mínimos (1 método por papel)

- **Estrutura Maven Multi-module**
  - Parent POM na raiz
  - Módulo `before/` independente
  - Módulo `after/` independente

- **Documentação**
  - README.md com comparativo completo
  - CHANGELOG.md

### Métricas de Ganho (After vs Before)
| Métrica | Before | After |
|---------|--------|-------|
| Métodos no fake de `FinalizarPedido` | 5 | 1 |
| Implementações com `throw UnsupportedOperationException` | 1 | 0 |
| Clientes afetados por mudança em `exportarCsv` | 6 | 1 |
| Acesso indevido a `excluir()` no relatório | Sim | Não (erro de compilação) |