package com.solid.isp.after;

import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.application.port.ConsultaPedidos;
import com.solid.isp.after.application.port.ExcluiPedido;
import com.solid.isp.after.application.port.ExportaPedidos;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTES DA VERSÃO "AFTER" - ISP Respeitado
 * Fakes mínimos: cada fake implementa APENAS o papel necessário
 */
class ISPRespectedTest {

    private Pedido criarPedidoExemplo(String id) {
        Cliente cliente = new Cliente("c1", "João Silva", "joao@email.com");
        Pedido pedido = new Pedido(id, cliente);
        pedido.adicionarItem(new Item("i1", "Produto A", new BigDecimal("100"), 2));
        pedido.adicionarItem(new Item("i2", "Produto B", new BigDecimal("50"), 1));
        return pedido;
    }

    // ============================================
    // FAKES MÍNIMOS - Um por papel (role interface)
    // ============================================

    /**
     * Fake para SalvaPedido - APENAS 1 método
     * Usado para testar FinalizarPedido
     */
    /**
     * Fake para BuscaPedido - APENAS 1 método
     */
    private BuscaPedido criarFakeBuscaPedido(List<Pedido> pedidos) {
        return id -> pedidos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Fake para ListaPedidos - APENAS 1 método
     */
    private ListaPedidos criarFakeListaPedidos(List<Pedido> pedidos) {
        return () -> List.copyOf(pedidos);
    }

    /**
     * Fake para ExcluiPedido - APENAS 1 método
     */
    /**
     * Fake para ExportaPedidos - contém somente operações de exportação
     */
    private ExportaPedidos criarFakeExportaPedidos() {
        return new ExportaPedidos() {
            @Override
            public String exportarCsv() {
                return "ID,Cliente,Data,Status,Total\n";
            }

            @Override
            public String exportarCabecalhoCsv() {
                return "ID,Cliente,Data,Status,Total";
            }
        };
    }

    // ============================================
    // TESTES DOS CASOS DE USO
    // ============================================

    @Test
    void teste1_FinalizarPedido_ComFakeMinimo() {
        System.out.println("\n=== TESTE 1: FinalizarPedido - Fake com APENAS SalvaPedido ===");
        
        List<Pedido> salvos = new ArrayList<>();
        SalvaPedido fakeSalva = salvos::add;
        FinalizarPedido finalizarPedido = new FinalizarPedido(fakeSalva);
        Pedido pedido = criarPedidoExemplo("1");
        
        assertDoesNotThrow(() -> finalizarPedido.executar(pedido));
        assertEquals(List.of(pedido), salvos);
        System.out.println("✓ FinalizarPedido executado com fake de 1 método");
    }

    @Test
    void teste2_CancelarPedido_ComFakesCompostos() {
        System.out.println("\n=== TESTE 2: CancelarPedido - Fakes BuscaPedido + ExcluiPedido ===");
        
        Pedido pedido = criarPedidoExemplo("2");
        BuscaPedido fakeBusca = criarFakeBuscaPedido(List.of(pedido));
        List<String> excluidos = new ArrayList<>();
        ExcluiPedido fakeExclui = excluidos::add;
        
        CancelarPedido cancelarPedido = new CancelarPedido(fakeBusca, fakeExclui);
        assertDoesNotThrow(() -> cancelarPedido.executar("2"));
        assertEquals(List.of("2"), excluidos);
        
        System.out.println("✓ CancelarPedido executado com 2 fakes de 1 método cada");
    }

    @Test
    void teste3_ConsultarPedido_ComConsultaPedidos() {
        System.out.println("\n=== TESTE 3: ConsultarPedido - Fake ConsultaPedidos (2 métodos relacionados) ===");
        
        List<Pedido> pedidos = List.of(criarPedidoExemplo("3"), criarPedidoExemplo("4"));
        
        // ConsultaPedidos = BuscaPedido + ListaPedidos (métodos sempre usados juntos)
            // Implementação via classe anônima que implementa a interface
            ConsultaPedidos fakeConsulta = new ConsultaPedidos() {
                @Override
                public Pedido buscarPorId(String id) {
                    return pedidos.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
                }
                @Override
                public List<Pedido> listar() {
                    return List.copyOf(pedidos);
                }
            };
        
            ConsultarPedido consultarPedido = new ConsultarPedido(fakeConsulta);
        
            assertEquals("3", consultarPedido.buscarPorId("3").getId());
            assertEquals(2, consultarPedido.listarTodos().size());
        
            System.out.println("✓ Busca por ID: " + consultarPedido.buscarPorId("3").getId());
            System.out.println("✓ Lista todos: " + consultarPedido.listarTodos().size() + " pedidos");
        }

    @Test
    void teste4_RelatorioFinanceiro_ComApenasListaPedidos_Seguranca() {
        System.out.println("\n=== TESTE 4: RelatorioFinanceiro - Fake APENAS ListaPedidos (segurança) ===");
        
        ListaPedidos fakeLista = criarFakeListaPedidos(List.of(criarPedidoExemplo("5"), criarPedidoExemplo("6")));
        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(fakeLista);
        
        RelatorioFinanceiro.Relatorio resultado = relatorio.gerar();
        
        assertEquals(2, resultado.getTotalPedidos());
        assertEquals(new BigDecimal("500"), resultado.getValorTotal());
        
        System.out.println("✓ Relatório: " + resultado.getTotalPedidos() + " pedidos, R$ " + resultado.getValorTotal());
        System.out.println("✓ RelatorioFinanceiro NÃO tem acesso a excluir(), salvar(), exportarCsv()");
    }

    @Test
    void teste5_ReplicaDeLeitura_ImplementaApenasLeitura() {
        System.out.println("\n=== TESTE 5: PedidoRepositoryReplica - Implementa APENAS BuscaPedido + ListaPedidos ===");
        
        PedidoRepositoryReplica replica = new PedidoRepositoryReplica();
        List<Pedido> pedidosParaReplica = List.of(criarPedidoExemplo("7"), criarPedidoExemplo("8"));
        replica.sincronizar(pedidosParaReplica);
        
        assertEquals(2, replica.listar().size());
        assertNotNull(replica.buscarPorId("7"));
        
        System.out.println("✓ Réplica sincronizada: " + replica.listar().size() + " pedidos");
        System.out.println("✓ Busca: " + replica.buscarPorId("7").getId());
        System.out.println("✓ Compilação impede uso de métodos de escrita na réplica");
        // replica.salvar(pedido) // ERRO DE COMPILAÇÃO: método não existe
        // replica.excluir("7")   // ERRO DE COMPILAÇÃO: método não existe
    }

    @Test
    void teste6_ImplementacaoCompleta_ImplementaTodosOsPapeis() {
        System.out.println("\n=== TESTE 6: MySqlPedidoRepository - Implementa TODOS os papéis ===");
        
        MySqlPedidoRepository mysql = new MySqlPedidoRepository();
        Pedido pedido = criarPedidoExemplo("9");
        
        mysql.salvar(pedido);
        assertNotNull(mysql.buscarPorId("9"));
        assertEquals(1, mysql.listar().size());
        assertTrue(mysql.exportarCsv().contains("9"));
        
        System.out.println("✓ Salvo: " + mysql.buscarPorId("9").getId());
        System.out.println("✓ Lista: " + mysql.listar().size() + " pedidos");
        System.out.println("✓ CSV exportado com sucesso");
    }

    @Test
    void teste7_Evolucao_MudancaEmExportaPedidosNaoAfetaOutros() {
        ExportaPedidos exportador = new MySqlPedidoRepository();

        assertEquals("ID,Cliente,Data,Status,Total", exportador.exportarCabecalhoCsv());
        assertEquals(List.of(SalvaPedido.class), dependencyTypes(FinalizarPedido.class));
        assertEquals(List.of(BuscaPedido.class, ExcluiPedido.class), dependencyTypes(CancelarPedido.class));
        assertEquals(List.of(ConsultaPedidos.class), dependencyTypes(ConsultarPedido.class));
        assertEquals(List.of(ListaPedidos.class), dependencyTypes(RelatorioFinanceiro.class));
    }

    private List<Class<?>> dependencyTypes(Class<?> useCase) {
        List<Class<?>> types = new ArrayList<>();
        for (Field field : useCase.getDeclaredFields()) {
            types.add(field.getType());
        }
        return List.copyOf(types);
    }
}