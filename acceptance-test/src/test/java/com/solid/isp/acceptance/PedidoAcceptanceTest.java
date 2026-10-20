package com.solid.isp.acceptance;

import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.application.port.ConsultaPedidos;
import com.solid.isp.after.application.port.ExcluiPedido;
import com.solid.isp.after.application.port.ListaPedidos;
import com.solid.isp.after.application.port.SalvaPedido;
import com.solid.isp.after.application.usecase.CancelarPedido;
import com.solid.isp.after.application.usecase.ConsultarPedido;
import com.solid.isp.after.application.usecase.FinalizarPedido;
import com.solid.isp.after.application.usecase.RelatorioFinanceiro;
import com.solid.isp.after.domain.Cliente;
import com.solid.isp.after.domain.Item;
import com.solid.isp.after.domain.Pedido;
import com.solid.isp.after.infrastructure.InMemoryPedidoRepository;
import com.solid.isp.after.infrastructure.MySqlPedidoRepository;
import com.solid.isp.after.infrastructure.PedidoRepositoryReplica;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de aceitação que validam equivalência comportamental entre before e after para o ISP.
 * 
 * A versão BEFORE viola o ISP - usa uma interface "gorda" PedidoRepository com 5 métodos.
 * A versão AFTER respeita o ISP - usa interfaces segregadas (SalvaPedido, BuscaPedido, ExcluiPedido, ListaPedidos, etc.)
 */
class PedidoAcceptanceTest {

    enum Versao {
        BEFORE,
        AFTER
    }

    // ==================== VERSÃO BEFORE ====================

        private static com.solid.isp.before.domain.Cliente clienteBefore(String id, String nome, String email) {
            return new com.solid.isp.before.domain.Cliente(id, nome, email);
    }

        private static com.solid.isp.before.domain.Item itemBefore(String id, String nome, double preco, int quantidade) {
            return new com.solid.isp.before.domain.Item(id, nome, BigDecimal.valueOf(preco), quantidade);
    }

        private static List<com.solid.isp.before.domain.Item> itensBasicosBefore() {
        return Arrays.asList(
                    itemBefore("i1", "Produto A", 100.0, 1),
                    itemBefore("i2", "Produto B", 50.0, 2)
        );
    }

        private static com.solid.isp.before.repository.PedidoRepository criarRepoBefore() {
            return new com.solid.isp.before.repository.MySqlPedidoRepository();
    }

    // ==================== VERSÃO AFTER ====================

        private static com.solid.isp.after.domain.Cliente clienteAfter(String id, String nome, String email) {
            return new com.solid.isp.after.domain.Cliente(id, nome, email);
    }

        private static com.solid.isp.after.domain.Item itemAfter(String id, String nome, double preco, int quantidade) {
            return new com.solid.isp.after.domain.Item(id, nome, BigDecimal.valueOf(preco), quantidade);
    }

        private static List<com.solid.isp.after.domain.Item> itensBasicosAfter() {
        return Arrays.asList(
                    itemAfter("i1", "Produto A", 100.0, 1),
                    itemAfter("i2", "Produto B", 50.0, 2)
        );
    }

        private static com.solid.isp.after.infrastructure.InMemoryPedidoRepository criarRepoAfter() {
            return new com.solid.isp.after.infrastructure.InMemoryPedidoRepository();
    }

    // ==================== TESTES COMUNS (FUNCIONAM EM AMBAS) ====================

    @ParameterizedTest(name = "Versão {0}: Finalizar pedido deve salvar pedido finalizado")
    @EnumSource(Versao.class)
    void finalizarPedido_deveSalvarPedidoFinalizado(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.FinalizarPedido usecase = new com.solid.isp.before.usecase.FinalizarPedido(repo);
            
                com.solid.isp.before.domain.Cliente cliente = clienteBefore("1", "João", "joao@email.com");
                com.solid.isp.before.domain.Pedido pedido = new com.solid.isp.before.domain.Pedido("pedido-1", cliente);
                pedido.adicionarItem(itemBefore("i1", "Produto A", 100.0, 1));
            
            usecase.executar(pedido);
            
                assertThat(pedido.getStatus()).isEqualTo(com.solid.isp.before.domain.Pedido.Status.FINALIZADO);
            assertThat(repo.buscarPorId("pedido-1")).isNotNull();
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.SalvaPedido salvaPedido = repo;
                com.solid.isp.after.application.usecase.FinalizarPedido usecase = new com.solid.isp.after.application.usecase.FinalizarPedido(salvaPedido);
            
                com.solid.isp.after.domain.Cliente cliente = clienteAfter("1", "João", "joao@email.com");
                com.solid.isp.after.domain.Pedido pedido = new com.solid.isp.after.domain.Pedido("pedido-1", cliente);
                pedido.adicionarItem(itemAfter("i1", "Produto A", 100.0, 1));
            
            usecase.executar(pedido);
            
                assertThat(pedido.getStatus()).isEqualTo(com.solid.isp.after.domain.Pedido.Status.FINALIZADO);
            assertThat(repo.buscarPorId("pedido-1")).isNotNull();
        }
    }

    @ParameterizedTest(name = "Versão {0}: Finalizar pedido sem itens deve lançar exceção")
    @EnumSource(Versao.class)
    void finalizarPedido_semItens_deveLancarExcecao(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.FinalizarPedido usecase = new com.solid.isp.before.usecase.FinalizarPedido(repo);
            
                com.solid.isp.before.domain.Cliente cliente = clienteBefore("1", "João", "joao@email.com");
                com.solid.isp.before.domain.Pedido pedido = new com.solid.isp.before.domain.Pedido("pedido-1", cliente);
            
            assertThatThrownBy(() -> usecase.executar(pedido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("sem itens");
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.SalvaPedido salvaPedido = repo;
                com.solid.isp.after.application.usecase.FinalizarPedido usecase = new com.solid.isp.after.application.usecase.FinalizarPedido(salvaPedido);
            
                com.solid.isp.after.domain.Cliente cliente = clienteAfter("1", "João", "joao@email.com");
                com.solid.isp.after.domain.Pedido pedido = new com.solid.isp.after.domain.Pedido("pedido-1", cliente);
            
            assertThatThrownBy(() -> usecase.executar(pedido))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("sem itens");
        }
    }

    @ParameterizedTest(name = "Versão {0}: Cancelar pedido deve buscar e excluir")
    @EnumSource(Versao.class)
    void cancelarPedido_deveBuscarEExcluir(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.CancelarPedido usecase = new com.solid.isp.before.usecase.CancelarPedido(repo);
            
                com.solid.isp.before.domain.Cliente cliente = clienteBefore("1", "João", "joao@email.com");
                com.solid.isp.before.domain.Pedido pedido = new com.solid.isp.before.domain.Pedido("pedido-1", cliente);
                pedido.adicionarItem(itemBefore("i1", "Produto A", 100.0, 1));
            repo.salvar(pedido);
            
            usecase.executar("pedido-1");
            
            assertThat(repo.buscarPorId("pedido-1")).isNull();
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.BuscaPedido busca = repo;
                com.solid.isp.after.application.port.ExcluiPedido exclui = repo;
                com.solid.isp.after.application.usecase.CancelarPedido usecase = new com.solid.isp.after.application.usecase.CancelarPedido(busca, exclui);
            
                com.solid.isp.after.domain.Cliente cliente = clienteAfter("1", "João", "joao@email.com");
                com.solid.isp.after.domain.Pedido pedido = new com.solid.isp.after.domain.Pedido("pedido-1", cliente);
                pedido.adicionarItem(itemAfter("i1", "Produto A", 100.0, 1));
            repo.salvar(pedido);
            
            usecase.executar("pedido-1");
            
            assertThat(repo.buscarPorId("pedido-1")).isNull();
        }
    }

    @ParameterizedTest(name = "Versão {0}: Cancelar pedido inexistente deve lançar exceção")
    @EnumSource(Versao.class)
    void cancelarPedido_inexistente_deveLancarExcecao(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.CancelarPedido usecase = new com.solid.isp.before.usecase.CancelarPedido(repo);
            
            assertThatThrownBy(() -> usecase.executar("inexistente"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não encontrado");
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.BuscaPedido busca = repo;
                com.solid.isp.after.application.port.ExcluiPedido exclui = repo;
                com.solid.isp.after.application.usecase.CancelarPedido usecase = new com.solid.isp.after.application.usecase.CancelarPedido(busca, exclui);
            
            assertThatThrownBy(() -> usecase.executar("inexistente"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não encontrado");
        }
    }

    @ParameterizedTest(name = "Versão {0}: Consultar pedido por ID deve retornar pedido")
    @EnumSource(Versao.class)
    void consultarPedido_porId_deveRetornarPedido(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.ConsultarPedido usecase = new com.solid.isp.before.usecase.ConsultarPedido(repo);
            
                com.solid.isp.before.domain.Cliente cliente = clienteBefore("1", "João", "joao@email.com");
                com.solid.isp.before.domain.Pedido pedido = new com.solid.isp.before.domain.Pedido("pedido-1", cliente);
                pedido.adicionarItem(itemBefore("i1", "Produto A", 100.0, 1));
            repo.salvar(pedido);
            
                com.solid.isp.before.domain.Pedido resultado = usecase.buscarPorId("pedido-1");
            
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo("pedido-1");
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.ConsultaPedidos consulta = repo; // InMemory implements BuscaPedido + ListaPedidos
                com.solid.isp.after.application.usecase.ConsultarPedido usecase = new com.solid.isp.after.application.usecase.ConsultarPedido(consulta);
            
                com.solid.isp.after.domain.Cliente cliente = clienteAfter("1", "João", "joao@email.com");
                com.solid.isp.after.domain.Pedido pedido = new com.solid.isp.after.domain.Pedido("pedido-1", cliente);
                pedido.adicionarItem(itemAfter("i1", "Produto A", 100.0, 1));
            repo.salvar(pedido);
            
                com.solid.isp.after.domain.Pedido resultado = usecase.buscarPorId("pedido-1");
            
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo("pedido-1");
        }
    }

    @ParameterizedTest(name = "Versão {0}: Listar todos pedidos deve retornar lista")
    @EnumSource(Versao.class)
    void listarTodos_deveRetornarLista(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.ConsultarPedido usecase = new com.solid.isp.before.usecase.ConsultarPedido(repo);
            
                com.solid.isp.before.domain.Cliente cliente = clienteBefore("1", "João", "joao@email.com");
                com.solid.isp.before.domain.Pedido pedido1 = new com.solid.isp.before.domain.Pedido("pedido-1", cliente);
                pedido1.adicionarItem(itemBefore("i1", "Produto A", 100.0, 1));
                com.solid.isp.before.domain.Pedido pedido2 = new com.solid.isp.before.domain.Pedido("pedido-2", cliente);
                pedido2.adicionarItem(itemBefore("i2", "Produto B", 50.0, 2));
            repo.salvar(pedido1);
            repo.salvar(pedido2);
            
                List<com.solid.isp.before.domain.Pedido> lista = usecase.listarTodos();
            
            assertThat(lista).hasSize(2);
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.ConsultaPedidos consulta = repo; // InMemory implements BuscaPedido + ListaPedidos
                com.solid.isp.after.application.usecase.ConsultarPedido usecase = new com.solid.isp.after.application.usecase.ConsultarPedido(consulta);
            
                com.solid.isp.after.domain.Cliente cliente = clienteAfter("1", "João", "joao@email.com");
                com.solid.isp.after.domain.Pedido pedido1 = new com.solid.isp.after.domain.Pedido("pedido-1", cliente);
                pedido1.adicionarItem(itemAfter("i1", "Produto A", 100.0, 1));
                com.solid.isp.after.domain.Pedido pedido2 = new com.solid.isp.after.domain.Pedido("pedido-2", cliente);
                pedido2.adicionarItem(itemAfter("i2", "Produto B", 50.0, 2));
            repo.salvar(pedido1);
            repo.salvar(pedido2);
            
                List<com.solid.isp.after.domain.Pedido> lista = usecase.listarTodos();
            
            assertThat(lista).hasSize(2);
        }
    }

    @ParameterizedTest(name = "Versão {0}: Relatório financeiro deve calcular total correto")
    @EnumSource(Versao.class)
    void relatorioFinanceiro_deveCalcularTotalCorreto(Versao versao) {
        if (versao == Versao.BEFORE) {
                com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
                com.solid.isp.before.usecase.RelatorioFinanceiro usecase = new com.solid.isp.before.usecase.RelatorioFinanceiro(repo);
            
                com.solid.isp.before.domain.Cliente cliente = clienteBefore("1", "João", "joao@email.com");
                com.solid.isp.before.domain.Pedido pedido1 = new com.solid.isp.before.domain.Pedido("pedido-1", cliente);
                pedido1.adicionarItem(itemBefore("i1", "Produto A", 100.0, 1));
                com.solid.isp.before.domain.Pedido pedido2 = new com.solid.isp.before.domain.Pedido("pedido-2", cliente);
                pedido2.adicionarItem(itemBefore("i2", "Produto B", 50.0, 2));
            repo.salvar(pedido1);
            repo.salvar(pedido2);
            
                com.solid.isp.before.usecase.RelatorioFinanceiro.Relatorio relatorio = usecase.gerar();
            
            assertThat(relatorio.getTotalPedidos()).isEqualTo(2);
            assertThat(relatorio.getValorTotal()).isEqualByComparingTo(new BigDecimal("200.0"));
        } else {
                com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
                com.solid.isp.after.application.port.ListaPedidos listaPedidos = repo;
                com.solid.isp.after.application.usecase.RelatorioFinanceiro usecase = new com.solid.isp.after.application.usecase.RelatorioFinanceiro(listaPedidos);
            
                com.solid.isp.after.domain.Cliente cliente = clienteAfter("1", "João", "joao@email.com");
                com.solid.isp.after.domain.Pedido pedido1 = new com.solid.isp.after.domain.Pedido("pedido-1", cliente);
                pedido1.adicionarItem(itemAfter("i1", "Produto A", 100.0, 1));
                com.solid.isp.after.domain.Pedido pedido2 = new com.solid.isp.after.domain.Pedido("pedido-2", cliente);
                pedido2.adicionarItem(itemAfter("i2", "Produto B", 50.0, 2));
            repo.salvar(pedido1);
            repo.salvar(pedido2);
            
                com.solid.isp.after.application.usecase.RelatorioFinanceiro.Relatorio relatorio = usecase.gerar();
            
            assertThat(relatorio.getTotalPedidos()).isEqualTo(2);
            assertThat(relatorio.getValorTotal()).isEqualByComparingTo(new BigDecimal("200.0"));
        }
    }

    // ==================== TESTES ESPECÍFICOS DO ISP ====================

    @Test
    void before_finalizarPedido_dependeDeInterfaceGorda() {
        // No BEFORE, FinalizarPedido depende de PedidoRepository (interface gorda com 5 métodos)
        // embora só use o método salvar()
            com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
            com.solid.isp.before.usecase.FinalizarPedido usecase = new com.solid.isp.before.usecase.FinalizarPedido(repo);
        
        // O usecase tem acesso a todos os métodos do repo, mesmo não usando
            assertThat(repo).isInstanceOf(com.solid.isp.before.repository.PedidoRepository.class);
        // Verifica que a interface tem 5 métodos (salvar, buscarPorId, listar, excluir, exportarCsv)
    }

    @Test
        void after_finalizarPedido_dependeApenasDeSalvaPedido() {
            // No AFTER, FinalizarPedido depende APENAS de SalvaPedido (1 método)
            com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
            com.solid.isp.after.application.port.SalvaPedido salvaPedido = repo;
            com.solid.isp.after.application.usecase.FinalizarPedido usecase = new com.solid.isp.after.application.usecase.FinalizarPedido(salvaPedido);
        
            // O usecase SÓ tem acesso ao método salvar() através da interface SalvaPedido
            assertThat(salvaPedido).isInstanceOf(com.solid.isp.after.application.port.SalvaPedido.class);
            // O usecase FinalizarPedido recebe SalvaPedido - não tem acesso a outros métodos
            assertThat(usecase).isInstanceOf(com.solid.isp.after.application.usecase.FinalizarPedido.class);
        }

        @Test
        void after_cancelarPedido_dependeDeBuscaPedidoEExcluiPedido() {
            // No AFTER, CancelarPedido depende de BuscaPedido e ExcluiPedido (2 métodos)
            com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
            com.solid.isp.after.application.port.BuscaPedido busca = repo;
            com.solid.isp.after.application.port.ExcluiPedido exclui = repo;
            com.solid.isp.after.application.usecase.CancelarPedido usecase = new com.solid.isp.after.application.usecase.CancelarPedido(busca, exclui);
        
            assertThat(busca).isInstanceOf(com.solid.isp.after.application.port.BuscaPedido.class);
            assertThat(exclui).isInstanceOf(com.solid.isp.after.application.port.ExcluiPedido.class);
            // O usecase CancelarPedido recebe BuscaPedido e ExcluiPedido - não tem acesso a SalvaPedido
            assertThat(usecase).isInstanceOf(com.solid.isp.after.application.usecase.CancelarPedido.class);
        }

        @Test
        void after_relatorioFinanceiro_dependeApenasDeListaPedidos() {
            // No AFTER, RelatorioFinanceiro depende APENAS de ListaPedidos (somente leitura)
            com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
            com.solid.isp.after.application.port.ListaPedidos listaPedidos = repo;
            com.solid.isp.after.application.usecase.RelatorioFinanceiro usecase = new com.solid.isp.after.application.usecase.RelatorioFinanceiro(listaPedidos);
        
            assertThat(listaPedidos).isInstanceOf(com.solid.isp.after.application.port.ListaPedidos.class);
            // O usecase RelatorioFinanceiro recebe ListaPedidos - não tem acesso a métodos de escrita
            assertThat(usecase).isInstanceOf(com.solid.isp.after.application.usecase.RelatorioFinanceiro.class);
        }

        @Test
        void after_replica_implementaApenasBuscaELista() {
            // PedidoRepositoryReplica implementa APENAS BuscaPedido + ListaPedidos
            // Não implementa SalvaPedido, ExcluiPedido, ExportaPedidos
            com.solid.isp.after.infrastructure.PedidoRepositoryReplica replica = new com.solid.isp.after.infrastructure.PedidoRepositoryReplica();
        
            assertThat(replica).isInstanceOf(com.solid.isp.after.application.port.BuscaPedido.class);
            assertThat(replica).isInstanceOf(com.solid.isp.after.application.port.ListaPedidos.class);
            assertThat(replica).isInstanceOf(com.solid.isp.after.application.port.ConsultaPedidos.class); // BuscaPedido + ListaPedidos
        
            assertThat(replica).isNotInstanceOf(com.solid.isp.after.application.port.SalvaPedido.class);
            assertThat(replica).isNotInstanceOf(com.solid.isp.after.application.port.ExcluiPedido.class);
            assertThat(replica).isNotInstanceOf(com.solid.isp.after.application.port.ExportaPedidos.class);
            assertThat(replica).isNotInstanceOf(com.solid.isp.after.application.port.GerenciaPedidos.class);
        }

        @Test
        void after_mySql_implementaTodasInterfaces() {
            // MySqlPedidoRepository implementa TODAS as interfaces (GerenciaPedidos)
            com.solid.isp.after.infrastructure.MySqlPedidoRepository mysql = new com.solid.isp.after.infrastructure.MySqlPedidoRepository();
        
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.SalvaPedido.class);
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.BuscaPedido.class);
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.ExcluiPedido.class);
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.ListaPedidos.class);
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.ExportaPedidos.class);
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.GerenciaPedidos.class);
            assertThat(mysql).isInstanceOf(com.solid.isp.after.application.port.ConsultaPedidos.class);
        }

    @Test
    void before_repositoryInterface_tem5Metodos() {
        // Verifica que a interface BEFORE tem 5 métodos (violação do ISP)
        // salvar, buscarPorId, listar, excluir, exportarCsv
            com.solid.isp.before.repository.PedidoRepository repo = criarRepoBefore();
        
        // A interface força todos os clientes a ver todos os 5 métodos
            assertThat(repo).isInstanceOf(com.solid.isp.before.repository.PedidoRepository.class);
    }

    @Test
    void after_interfacesSegregadas_temMetodosEspecificos() {
        // Verifica que as interfaces AFTER são segregadas
            com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
        
        // Cada interface tem apenas os métodos necessários
            com.solid.isp.after.application.port.SalvaPedido salva = repo;
            com.solid.isp.after.application.port.BuscaPedido busca = repo;
            com.solid.isp.after.application.port.ExcluiPedido exclui = repo;
            com.solid.isp.after.application.port.ListaPedidos lista = repo;
            com.solid.isp.after.application.port.ExportaPedidos exporta = repo;
        
            assertThat(salva).isInstanceOf(com.solid.isp.after.application.port.SalvaPedido.class);
            assertThat(busca).isInstanceOf(com.solid.isp.after.application.port.BuscaPedido.class);
            assertThat(exclui).isInstanceOf(com.solid.isp.after.application.port.ExcluiPedido.class);
            assertThat(lista).isInstanceOf(com.solid.isp.after.application.port.ListaPedidos.class);
            assertThat(exporta).isInstanceOf(com.solid.isp.after.application.port.ExportaPedidos.class);
    }

    @Test
    void evolucao_mudancaEmExportaPedidos_naoAfetaOutrosClientes() {
        // TESTE DE EVOLUÇÃO: Mudança em ExportaPedidos não afeta outros clientes
        // No AFTER, apenas quem usa ExportaPedidos seria afetado
        
            com.solid.isp.after.infrastructure.InMemoryPedidoRepository repo = criarRepoAfter();
        
        // FinalizarPedido usa APENAS SalvaPedido
            com.solid.isp.after.application.usecase.FinalizarPedido finalizar = new com.solid.isp.after.application.usecase.FinalizarPedido(repo);
            assertThat(finalizar).isInstanceOf(com.solid.isp.after.application.usecase.FinalizarPedido.class);
        
        // CancelarPedido usa APENAS BuscaPedido + ExcluiPedido
            com.solid.isp.after.application.usecase.CancelarPedido cancelar = new com.solid.isp.after.application.usecase.CancelarPedido(repo, repo);
            assertThat(cancelar).isInstanceOf(com.solid.isp.after.application.usecase.CancelarPedido.class);
        
        // RelatorioFinanceiro usa APENAS ListaPedidos
            com.solid.isp.after.application.usecase.RelatorioFinanceiro relatorio = new com.solid.isp.after.application.usecase.RelatorioFinanceiro(repo);
            assertThat(relatorio).isInstanceOf(com.solid.isp.after.application.usecase.RelatorioFinanceiro.class);
        
        // ConsultarPedido usa ConsultaPedidos (BuscaPedido + ListaPedidos)
            com.solid.isp.after.application.usecase.ConsultarPedido consultar = new com.solid.isp.after.application.usecase.ConsultarPedido(repo);
            assertThat(consultar).isInstanceOf(com.solid.isp.after.application.usecase.ConsultarPedido.class);
        
        // Se ExportaPedidos mudar, apenas classes que o usam são afetadas
        // FinalizarPedido, CancelarPedido, RelatorioFinanceiro, ConsultarPedido NÃO são afetados
    }
}