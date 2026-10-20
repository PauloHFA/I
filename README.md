# SOLID #4 – Interface Segregation Principle (ISP) - Java

> **Princípio da Segregação de Interfaces**  
> *"Os clientes não devem ser forçados a depender de interfaces que não utilizam."* — Robert C. Martin

## 🎯 Objetivo

Este repositório demonstra o **Interface Segregation Principle (ISP)** através de um sistema de pedidos implementado em **duas versões** usando **Java 17 + Maven**:

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
├── pom.xml                    # Maven parent POM
├── README.md
├── CHANGELOG.md
├── before/                    # Versão que VIOLA o ISP
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/solid/isp/before/
│       │   ├── domain/           # Cliente, Item, Pedido
│       │   ├── repository/       # PedidoRepository (gorda), MySqlPedidoRepository, PedidoRepositoryReplica
│       │   └── usecase/          # FinalizarPedido, CancelarPedido, ConsultarPedido, RelatorioFinanceiro
│       └── test/java/com/solid/isp/before/
│           └── ISPViolationsTest.java  # Demonstra os 5 problemas
│
└── after/                     # Versão que RESPEITA o ISP
    ├── pom.xml
    └── src/
        ├── main/java/com/solid/isp/after/
        │   ├── domain/                    # Cliente, Item, Pedido
        │   ├── application/
        │   │   ├── port/                  # Role interfaces (SalvaPedido, BuscaPedido, etc.)
        │   │   └── usecase/               # Casos de uso com dependências mínimas
        │   └── infrastructure/            # MySqlPedidoRepository, PedidoRepositoryReplica, InMemoryPedidoRepository
        └── test/java/com/solid/isp/after/
            └── ISPRespectedTest.java      # Testes com fakes mínimos
```

---

## 🔴 Versão BEFORE – Problemas Demonstrados

### Interface Gorda
```java
public interface PedidoRepository {
    void salvar(Pedido pedido);
    Pedido buscarPorId(String id);
    List<Pedido> listar();
    void excluir(String id);
    String exportarCsv();
}
```

### Problemas Reais

| Problema | Descrição |
|----------|-----------|
| **Fake gigante** | Testar `FinalizarPedido` exige implementar 5 métodos (só usa 1) |
| **Violação LSP** | `PedidoRepositoryReplica` lança `UnsupportedOperationException` em `salvar()` e `excluir()` |
| **Acesso indevido** | `RelatorioFinanceiro` pode chamar `excluir()` acidentalmente |
| **Acoplamento** | Mudança em `exportarCsv()` afeta TODOS os 6 clientes/implementações |
| **Nomes genéricos** | `PedidoRepository` não revela o papel de cada cliente |

---

## 🟢 Versão AFTER – Solução ISP

### Role Interfaces (Portas)
```java
public interface SalvaPedido     { void salvar(Pedido pedido); }
public interface BuscaPedido     { Pedido buscarPorId(String id); }
public interface ListaPedidos    { List<Pedido> listar(); }
public interface ExcluiPedido    { void excluir(String id); }
public interface ExportaPedidos  { String exportarCsv(); }

// Composição quando métodos são sempre usados juntos
public interface ConsultaPedidos extends BuscaPedido, ListaPedidos {}
public interface GerenciaPedidos extends SalvaPedido, BuscaPedido, ExcluiPedido {}
```

### Clientes Dependem Apenas do que Usam
```java
public class FinalizarPedido {
    private final SalvaPedido repo;  // Só salvar
    public FinalizarPedido(SalvaPedido repo) { this.repo = repo; }
}

public class CancelarPedido {
    private final BuscaPedido busca;
    private final ExcluiPedido exclui;  // Buscar + Excluir
    public CancelarPedido(BuscaPedido busca, ExcluiPedido exclui) { ... }
}

public class RelatorioFinanceiro {
    private final ListaPedidos pedidos;  // Só leitura!
    public RelatorioFinanceiro(ListaPedidos pedidos) { this.pedidos = pedidos; }
}
```

### Implementações
```java
// Completa: implementa todos os papéis
public class MySqlPedidoRepository implements 
    SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos { }

// Réplica: APENAS leitura - compilação FALHA se tentar escrever
public class PedidoRepositoryReplica implements BuscaPedido, ListaPedidos { }
```

---

## 🧪 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Executar Testes - Versão AFTER (ISP Respeitado)
```bash
cd after
mvn test
```

### Executar Testes - Versão BEFORE (Problemas)
```bash
cd before
mvn test
```

### Executar Todos os Testes
```bash
mvn test
```

---

## 📊 Comparativo de Métricas

| Métrica | Before (Viola ISP) | After (Respeita ISP) |
|---------|-------------------|---------------------|
| Métodos da interface usados por `FinalizarPedido` | 1 de 5 | 1 de 1 |
| Implementações com `throw UnsupportedOperationException` | 1+ | 0 |
| Métodos no fake de teste de `FinalizarPedido` | 5 | 1 |
| Clientes afetados ao mudar `exportarCsv` | **Todos (6)** | Apenas quem usa `ExportaPedidos` |
| `RelatorioFinanceiro` tem acesso a `excluir()` | ✅ Sim (perigoso) | ❌ Não (erro de compilação) |
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
- [ ] Nenhuma implementação lança `UnsupportedOperationException`, tem método vazio ou retorna valor falso?
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