package com.solid.isp.before;

import com.solid.isp.before.domain.Cliente;
import com.solid.isp.before.domain.Item;
import com.solid.isp.before.domain.Pedido;
import com.solid.isp.before.repository.MySqlPedidoRepository;
import com.solid.isp.before.repository.PedidoRepository;
import com.solid.isp.before.repository.PedidoRepositoryReplica;
import com.solid.isp.before.usecase.CancelarPedido;
import com.solid.isp.before.usecase.ConsultarPedido;
import com.solid.isp.before.usecase.FinalizarPedido;
import com.solid.isp.before.usecase.RelatorioFinanceiro;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTES DA VERSÃO "BEFORE" - ISP Violado
 * Demonstra os problemas: fakes grandes, violação LSP, acesso indevido a métodos
 */
class ISPViolationsTest {

    private Pedido criarPedidoExemplo(String id) {
        Cliente cliente = new Cliente("c1", "João Silva", "joao@email.com");
        Pedido pedido = new Pedido(id, cliente);
        pedido.adicionarItem(new Item("i1", "Produto A", new BigDecimal("100"), 2));
        pedido.adicionarItem(new Item("i2", "Produto B", new BigDecimal("50"), 1));
        return pedido;
    }

    // ============================================
    // PROBLEMA 1: Fake GIGANTE para testar FinalizarPedido
    // ============================================
    @Test
    void problema1_FakeGiganteParaTestarUmMetodo() {
        System.out.println("\n=== PROBLEMA 1: Fake Gigante ===");
        
        // Fake precisa implementar TODOS os 5 métodos da interface
        PedidoRepository fakeGigante = new PedidoRepository() {
            @Override public void salvar(Pedido p) { }
            @Override public Pedido buscarPorId(String id) { return null; }
            @Override public List<Pedido> listar() { return List.of(); }
            @Override public void excluir(String id) { }
            @Override public String exportarCsv() { return ""; }
        };

        FinalizarPedido finalizarPedido = new FinalizarPedido(fakeGigante);
        Pedido pedido = criarPedidoExemplo("1");
        finalizarPedido.executar(pedido);
        
        System.out.println("✗ Fake tem 5 métodos, mas FinalizarPedido só usa 1 (salvar)");
        System.out.println("✗ Métodos irrelevantes: buscarPorId, listar, excluir, exportarCsv");
    }

    // ============================================
    // PROBLEMA 2: Réplica lança exceção (violação LSP)
    // ============================================
    @Test
    void problema2_ReplicaLancaExcecao_ViolacaoLSP() {
        System.out.println("\n=== PROBLEMA 2: Violação LSP na Réplica ===");
        
        PedidoRepositoryReplica replica = new PedidoRepositoryReplica();
        replica.sincronizar(List.of(criarPedidoExemplo("2")));

        // Tentar salvar na réplica - lança exceção
        assertThrows(UnsupportedOperationException.class, () -> {
            replica.salvar(criarPedidoExemplo("3"));
        });
        System.out.println("✗ Violação LSP: replica.salvar() lança UnsupportedOperationException");

        // Tentar excluir na réplica - lança exceção
        assertThrows(UnsupportedOperationException.class, () -> {
            replica.excluir("2");
        });
        System.out.println("✗ Violação LSP: replica.excluir() lança UnsupportedOperationException");
        
        System.out.println("✗ Réplica não pode ser usada onde se espera PedidoRepository completo");
    }

    // ============================================
    // PROBLEMA 3: RelatorioFinanceiro tem acesso a excluir() - PERIGOSO!
    // ============================================
    @Test
    void problema3_RelatorioTemAcessoAMetodosDeEscrita() {
        System.out.println("\n=== PROBLEMA 3: Acesso Indevido a Métodos de Escrita ===");
        
        MySqlPedidoRepository mysql = new MySqlPedidoRepository();
        mysql.salvar(criarPedidoExemplo("4"));
        mysql.salvar(criarPedidoExemplo("5"));

        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(mysql);
        
        // PERIGO: relatorio.repo.excluir("4") - COMPILA E FUNCIONA, mas é um bug!
        // O relatório tem acesso ao repo completo
        assertNotNull(relatorio.gerar());
        
        System.out.println("✗ RelatorioFinanceiro PODE chamar repo.excluir() - risco de exclusão acidental");
        System.out.println("✗ RelatorioFinanceiro PODE chamar repo.salvar() - violação de responsabilidade");
        System.out.println("✗ RelatorioFinanceiro PODE chamar repo.exportarCsv() - desnecessário");
    }

    // ============================================
    // PROBLEMA 4: Mudança em exportarCsv afeta TODOS os clientes
    // ============================================
    @Test
    void problema4_MudancaNaInterfaceGordaAfetaTodos() {
        System.out.println("\n=== PROBLEMA 4: Acoplamento Desnecessário ===");
        
        System.out.println("✗ Se exportarCsv() mudar de assinatura:");
        System.out.println("  - FinalizarPedido precisa recompilar (não usa exportarCsv)");
        System.out.println("  - CancelarPedido precisa recompilar (não usa exportarCsv)");
        System.out.println("  - ConsultarPedido precisa recompilar (não usa exportarCsv)");
        System.out.println("  - RelatorioFinanceiro precisa recompilar (não usa exportarCsv)");
        System.out.println("  - PedidoRepositoryReplica precisa mudar (não usa exportarCsv de forma significativa)");
        System.out.println("  - MySqlPedidoRepository precisa mudar");
        System.out.println("✗ Acoplamento desnecessário entre clientes não relacionados");
    }

    // ============================================
    // PROBLEMA 5: Interface nomeada por tecnologia, não por papel
    // ============================================
    @Test
    void problema5_InterfaceNomeadaPorTecnologia() {
        System.out.println("\n=== PROBLEMA 5: Nomes Genéricos ===");
        
        System.out.println("✗ PedidoRepository não revela o que cada cliente precisa");
        System.out.println("✗ Dificulta entender as dependências reais");
    }
}