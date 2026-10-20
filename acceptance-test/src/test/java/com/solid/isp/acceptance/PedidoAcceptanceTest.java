package com.solid.isp.acceptance;

import com.solid.isp.after.application.port.BuscaPedido;
import com.solid.isp.after.application.port.ConsultaPedidos;
import com.solid.isp.after.application.port.ExcluiPedido;
import com.solid.isp.after.application.port.ExportaPedidos;
import com.solid.isp.after.application.port.GerenciaPedidos;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoAcceptanceTest {

    private static com.solid.isp.before.domain.Pedido pedidoBefore(String id) {
        var pedido = new com.solid.isp.before.domain.Pedido(
                id, new com.solid.isp.before.domain.Cliente("cliente-1", "João", "joao@example.com"));
        pedido.adicionarItem(new com.solid.isp.before.domain.Item(
                "item-1", "Produto A", new BigDecimal("100.00"), 2));
        return pedido;
    }

    private static Pedido pedidoAfter(String id) {
        var pedido = new Pedido(id, new Cliente("cliente-1", "João", "joao@example.com"));
        pedido.adicionarItem(new Item("item-1", "Produto A", new BigDecimal("100.00"), 2));
        return pedido;
    }

    @Test
    void finalizarPedido_preservaComportamentoNasDuasVersoes() {
        var beforeRepository = new com.solid.isp.before.repository.MySqlPedidoRepository();
        var beforePedido = pedidoBefore("before-1");
        new com.solid.isp.before.usecase.FinalizarPedido(beforeRepository).executar(beforePedido);

        var afterRepository = new InMemoryPedidoRepository();
        var afterPedido = pedidoAfter("after-1");
        new FinalizarPedido((SalvaPedido) afterRepository).executar(afterPedido);

        assertThat(beforePedido.getStatus()).isEqualTo(com.solid.isp.before.domain.Pedido.Status.FINALIZADO);
        assertThat(afterPedido.getStatus()).isEqualTo(Pedido.Status.FINALIZADO);
        assertThat(beforeRepository.buscarPorId("before-1")).isSameAs(beforePedido);
        assertThat(afterRepository.buscarPorId("after-1")).isSameAs(afterPedido);
    }

    @Test
    void finalizarPedido_semItens_lancaExcecaoNasDuasVersoes() {
        var beforePedido = new com.solid.isp.before.domain.Pedido(
                "before-1", new com.solid.isp.before.domain.Cliente("cliente-1", "João", "joao@example.com"));
        var afterPedido = new Pedido("after-1", new Cliente("cliente-1", "João", "joao@example.com"));

        assertThatThrownBy(() -> new com.solid.isp.before.usecase.FinalizarPedido(
                new com.solid.isp.before.repository.MySqlPedidoRepository()).executar(beforePedido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sem itens");
        assertThatThrownBy(() -> new FinalizarPedido((SalvaPedido) new InMemoryPedidoRepository())
                .executar(afterPedido))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sem itens");
    }

    @Test
    void cancelarPedido_preservaComportamentoNasDuasVersoes() {
        var beforeRepository = new com.solid.isp.before.repository.MySqlPedidoRepository();
        beforeRepository.salvar(pedidoBefore("before-1"));
        new com.solid.isp.before.usecase.CancelarPedido(beforeRepository).executar("before-1");

        var afterRepository = new InMemoryPedidoRepository();
        afterRepository.salvar(pedidoAfter("after-1"));
        new CancelarPedido((BuscaPedido) afterRepository, (ExcluiPedido) afterRepository)
                .executar("after-1");

        assertThat(beforeRepository.buscarPorId("before-1")).isNull();
        assertThat(afterRepository.buscarPorId("after-1")).isNull();
    }

    @Test
    void cancelarPedido_inexistente_lancaExcecaoNasDuasVersoes() {
        assertThatThrownBy(() -> new com.solid.isp.before.usecase.CancelarPedido(
                new com.solid.isp.before.repository.MySqlPedidoRepository()).executar("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrado");
        assertThatThrownBy(() -> new CancelarPedido(
                (BuscaPedido) new InMemoryPedidoRepository(), (ExcluiPedido) new InMemoryPedidoRepository())
                .executar("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    void consultarPedido_preservaBuscaEListagemNasDuasVersoes() {
        var beforeRepository = new com.solid.isp.before.repository.MySqlPedidoRepository();
        var beforePedido = pedidoBefore("before-1");
        beforeRepository.salvar(beforePedido);
        var beforeQuery = new com.solid.isp.before.usecase.ConsultarPedido(beforeRepository);

        var afterRepository = new InMemoryPedidoRepository();
        var afterPedido = pedidoAfter("after-1");
        afterRepository.salvar(afterPedido);
        var afterQuery = new ConsultarPedido((ConsultaPedidos) afterRepository);

        assertThat(beforeQuery.buscarPorId("before-1")).isSameAs(beforePedido);
        assertThat(beforeQuery.listarTodos()).containsExactly(beforePedido);
        assertThat(afterQuery.buscarPorId("after-1")).isSameAs(afterPedido);
        assertThat(afterQuery.listarTodos()).containsExactly(afterPedido);
    }

    @Test
    void relatorioFinanceiro_preservaContagemETotalNasDuasVersoes() {
        var beforeRepository = new com.solid.isp.before.repository.MySqlPedidoRepository();
        beforeRepository.salvar(pedidoBefore("before-1"));
        var beforeReport = new com.solid.isp.before.usecase.RelatorioFinanceiro(beforeRepository).gerar();

        var afterRepository = new InMemoryPedidoRepository();
        afterRepository.salvar(pedidoAfter("after-1"));
        var afterReport = new RelatorioFinanceiro((ListaPedidos) afterRepository).gerar();

        assertThat(beforeReport.getTotalPedidos()).isEqualTo(1);
        assertThat(beforeReport.getValorTotal()).isEqualByComparingTo("200.00");
        assertThat(afterReport.getTotalPedidos()).isEqualTo(1);
        assertThat(afterReport.getValorTotal()).isEqualByComparingTo("200.00");
    }

    @Test
    void finalizarPedido_aceitaFakeComApenasPapelDeSalvar() {
        var saved = new AtomicReference<Pedido>();
        SalvaPedido fake = saved::set;
        var pedido = pedidoAfter("after-1");

        new FinalizarPedido(fake).executar(pedido);

        assertThat(saved.get()).isSameAs(pedido);
        assertThat(pedido.getStatus()).isEqualTo(Pedido.Status.FINALIZADO);
    }

    @Test
    void cancelarPedido_usaFakesSeparadosParaBuscaEExclusao() {
        var pedido = pedidoAfter("after-1");
        var deletedId = new AtomicReference<String>();
        BuscaPedido busca = id -> "after-1".equals(id) ? pedido : null;
        ExcluiPedido exclui = deletedId::set;

        new CancelarPedido(busca, exclui).executar("after-1");

        assertThat(deletedId.get()).isEqualTo("after-1");
        assertThat(pedido.getStatus()).isEqualTo(Pedido.Status.CANCELADO);
    }

    @Test
    void relatorioFinanceiro_aceitaFakeSomenteDeLeitura() {
        ListaPedidos fake = () -> List.of(pedidoAfter("after-1"));

        var report = new RelatorioFinanceiro(fake).gerar();

        assertThat(report.getTotalPedidos()).isEqualTo(1);
        assertThat(report.getValorTotal()).isEqualByComparingTo("200.00");
    }

    @Test
    void casosDeUsoDependemApenasDosPapeisQueConsomem() {
        assertThat(dependencyTypes(FinalizarPedido.class)).containsExactly(SalvaPedido.class);
        assertThat(dependencyTypes(CancelarPedido.class)).containsExactly(BuscaPedido.class, ExcluiPedido.class);
        assertThat(dependencyTypes(ConsultarPedido.class)).containsExactly(ConsultaPedidos.class);
        assertThat(dependencyTypes(RelatorioFinanceiro.class)).containsExactly(ListaPedidos.class);
    }

    @Test
    void replicaImplementaSomentePapeisDeLeitura() {
        assertThat(BuscaPedido.class.isAssignableFrom(PedidoRepositoryReplica.class)).isTrue();
        assertThat(ListaPedidos.class.isAssignableFrom(PedidoRepositoryReplica.class)).isTrue();
        assertThat(ConsultaPedidos.class.isAssignableFrom(PedidoRepositoryReplica.class)).isTrue();
        assertThat(SalvaPedido.class.isAssignableFrom(PedidoRepositoryReplica.class)).isFalse();
        assertThat(ExcluiPedido.class.isAssignableFrom(PedidoRepositoryReplica.class)).isFalse();
    }

    @Test
    void repositorioCompletoCompoePapeisSemEngordarOsClientes() {
        assertThat(GerenciaPedidos.class.isAssignableFrom(MySqlPedidoRepository.class)).isTrue();
        assertThat(ConsultaPedidos.class.isAssignableFrom(MySqlPedidoRepository.class)).isTrue();
    }

    @Test
    void evoluirPapelDeExportacaoNaoAlteraDependenciasDosCasosDeUso() {
        ExportaPedidos inMemoryExporter = new InMemoryPedidoRepository();
        ExportaPedidos mysqlExporter = new MySqlPedidoRepository();

        assertThat(inMemoryExporter.exportarCabecalhoCsv()).isEqualTo("ID,Cliente,Data,Status,Total");
        assertThat(mysqlExporter.exportarCabecalhoCsv()).isEqualTo("ID,Cliente,Data,Status,Total");
        assertThat(dependencyTypes(FinalizarPedido.class)).containsExactly(SalvaPedido.class);
        assertThat(dependencyTypes(CancelarPedido.class)).containsExactly(BuscaPedido.class, ExcluiPedido.class);
        assertThat(dependencyTypes(ConsultarPedido.class)).containsExactly(ConsultaPedidos.class);
        assertThat(dependencyTypes(RelatorioFinanceiro.class)).containsExactly(ListaPedidos.class);
    }

    private static List<Class<?>> dependencyTypes(Class<?> useCase) {
        return Arrays.stream(useCase.getDeclaredFields())
                .map(Field::getType)
                .toList();
    }
}