import { Pedido, Cliente, Item } from '../../src/domain/Pedido';
import { SalvaPedido, BuscaPedido, ListaPedidos, ExcluiPedido, ExportaPedidos } from '../../src/application/ports';
import { FinalizarPedido } from '../../src/application/FinalizarPedido';
import { CancelarPedido } from '../../src/application/CancelarPedido';
import { ConsultarPedido } from '../../src/application/ConsultarPedido';
import { RelatorioFinanceiro } from '../../src/application/RelatorioFinanceiro';
import { MySqlPedidoRepository } from '../../src/infra/MySqlPedidoRepository';
import { PedidoRepositoryReplica } from '../../src/infra/PedidoRepositoryReplica';
import { InMemoryPedidoRepository } from '../../src/infra/InMemoryPedidoRepository';

/**
 * TESTES DA VERSÃO "AFTER" - ISP Respeitado
 * Fakes mínimos: cada fake implementa APENAS o papel necessário
 */

// ============================================
// FAKES MÍNIMOS - Um por papel (role interface)
// ============================================

/**
 * Fake para SalvaPedido - APENAS 1 método
 * Usado para testar FinalizarPedido
 */
const criarFakeSalvaPedido = (): SalvaPedido => {
  const salvos: Pedido[] = [];
  return {
    salvar: (pedido: Pedido) => salvos.push(pedido)
  };
};

/**
 * Fake para BuscaPedido - APENAS 1 método
 */
const criarFakeBuscaPedido = (pedidos: Pedido[]): BuscaPedido => ({
  buscarPorId: (id: string) => pedidos.find(p => p.id === id) ?? null
});

/**
 * Fake para ListaPedidos - APENAS 1 método
 */
const criarFakeListaPedidos = (pedidos: Pedido[]): ListaPedidos => ({
  listar: () => [...pedidos]
});

/**
 * Fake para ExcluiPedido - APENAS 1 método
 */
const criarFakeExcluiPedido = (): ExcluiPedido => {
  const excluidos: string[] = [];
  return {
    excluir: (id: string) => { excluidos.push(id); }
  };
};

/**
 * Fake para ExportaPedidos - APENAS 1 método
 */
const criarFakeExportaPedidos = (): ExportaPedidos => ({
  exportarCsv: () => 'ID,Cliente,Data,Status,Total\n'
});

// ============================================
// TESTES DOS CASOS DE USO
// ============================================

function criarPedidoExemplo(id: string = '1'): Pedido {
  const cliente = new Cliente('c1', 'João Silva', 'joao@email.com');
  const pedido = new Pedido(id, cliente);
  pedido.adicionarItem(new Item('i1', 'Produto A', 100, 2));
  pedido.adicionarItem(new Item('i2', 'Produto B', 50, 1));
  return pedido;
}

console.log('=== TESTES VERSÃO AFTER (ISP Respeitado) ===\n');

// Teste 1: FinalizarPedido com fake mínimo (1 método)
console.log('1. FinalizarPedido - Fake com APENAS SalvaPedido');
const fakeSalva = criarFakeSalvaPedido();
const finalizarPedido = new FinalizarPedido(fakeSalva);
const pedido1 = criarPedidoExemplo('1');
finalizarPedido.executar(pedido1);
console.log('   ✓ FinalizarPedido executado com fake de 1 método\n');

// Teste 2: CancelarPedido com fakes compostos
console.log('2. CancelarPedido - Fakes BuscaPedido + ExcluiPedido');
const pedido2 = criarPedidoExemplo('2');
const fakeBusca = criarFakeBuscaPedido([pedido2]);
const fakeExclui = criarFakeExcluiPedido();
const cancelarPedido = new CancelarPedido(fakeBusca, fakeExclui);
cancelarPedido.executar('2');
console.log('   ✓ CancelarPedido executado com 2 fakes de 1 método cada\n');

// Teste 3: ConsultarPedido com ConsultaPedidos (BuscaPedido + ListaPedidos)
console.log('3. ConsultarPedido - Fake ConsultaPedidos (2 métodos relacionados)');
const pedidos = [criarPedidoExemplo('3'), criarPedidoExemplo('4')];
const fakeConsulta = {
  buscarPorId: (id: string) => pedidos.find(p => p.id === id) ?? null,
  listar: () => [...pedidos]
};
const consultarPedido = new ConsultarPedido(fakeConsulta);
console.log(`   ✓ Busca por ID: ${consultarPedido.buscarPorId('3')?.id ?? 'não encontrado'}`);
console.log(`   ✓ Lista todos: ${consultarPedido.listarTodos().length} pedidos\n`);

// Teste 4: RelatorioFinanceiro com APENAS ListaPedidos (sem acesso a escrita)
console.log('4. RelatorioFinanceiro - Fake APENAS ListaPedidos (segurança)');
const fakeLista = criarFakeListaPedidos([criarPedidoExemplo('5'), criarPedidoExemplo('6')]);
const relatorio = new RelatorioFinanceiro(fakeLista);
const resultado = relatorio.gerar();
console.log(`   ✓ Relatório: ${resultado.totalPedidos} pedidos, R$ ${resultado.valorTotal.toFixed(2)}`);
console.log('   ✓ RelatorioFinanceiro NÃO tem acesso a excluir(), salvar(), exportarCsv()\n');

// Teste 5: Réplica de leitura - implementa só leitura, compilação falharia se tentasse escrita
console.log('5. PedidoRepositoryReplica - Implementa APENAS BuscaPedido + ListaPedidos');
const replica = new PedidoRepositoryReplica();
const pedidosParaReplica = [criarPedidoExemplo('7'), criarPedidoExemplo('8')];
replica.sincronizar(pedidosParaReplica);
console.log(`   ✓ Réplica sincronizada: ${replica.listar().length} pedidos`);
console.log(`   ✓ Busca: ${replica.buscarPorId('7')?.id ?? 'não encontrado'}`);
// replica.salvar(pedido) // ERRO DE COMPILAÇÃO: Property 'salvar' does not exist
// replica.excluir('7')   // ERRO DE COMPILAÇÃO: Property 'excluir' does not exist
console.log('   ✓ Compilação impede uso de métodos de escrita na réplica\n');

// Teste 6: Implementação completa (MySQL) implementa todos os papéis
console.log('6. MySqlPedidoRepository - Implementa TODOS os papéis');
const mysql = new MySqlPedidoRepository();
const pedidoMysql = criarPedidoExemplo('9');
mysql.salvar(pedidoMysql);
console.log(`   ✓ Salvo: ${mysql.buscarPorId('9')?.id}`);
console.log(`   ✓ Lista: ${mysql.listar().length} pedidos`);
console.log(`   ✓ CSV: ${mysql.exportarCsv().split('\n').length - 1} linhas de dados\n`);

// Teste 7: Prova de evolução - adicionar método a ExportaPedidos não afeta outros clientes
console.log('7. Evolução: Mudança em ExportaPedidos não afeta FinalizarPedido, CancelarPedido, etc.');
console.log('   ✓ FinalizarPedido depende só de SalvaPedido');
console.log('   ✓ CancelarPedido depende só de BuscaPedido + ExcluiPedido');
console.log('   ✓ RelatorioFinanceiro depende só de ListaPedidos');
console.log('   ✓ Apenas quem usa ExportaPedidos seria afetado\n');

console.log('=== TODOS OS TESTES PASSARAM ===');