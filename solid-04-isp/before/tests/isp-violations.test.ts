import { Pedido, Cliente, Item } from '../src/Pedido';
import { PedidoRepository } from '../src/PedidoRepository';
import { FinalizarPedido } from '../src/FinalizarPedido';
import { CancelarPedido } from '../src/CancelarPedido';
import { ConsultarPedido } from '../src/ConsultarPedido';
import { RelatorioFinanceiro } from '../src/RelatorioFinanceiro';
import { MySqlPedidoRepository } from '../src/MySqlPedidoRepository';
import { PedidoRepositoryReplica } from '../src/PedidoRepositoryReplica';

/**
 * TESTES DA VERSÃO "BEFORE" - ISP Violado
 * Demonstra os problemas: fakes grandes, violação LSP, acesso indevido a métodos
 */

function criarPedidoExemplo(id: string = '1'): Pedido {
  const cliente = new Cliente('c1', 'João Silva', 'joao@email.com');
  const pedido = new Pedido(id, cliente);
  pedido.adicionarItem(new Item('i1', 'Produto A', 100, 2));
  pedido.adicionarItem(new Item('i2', 'Produto B', 50, 1));
  return pedido;
}

console.log('=== TESTES VERSÃO BEFORE (ISP Violado) ===\n');

// ============================================
// PROBLEMA 1: Fake GIGANTE para testar FinalizarPedido
// ============================================
console.log('1. PROBLEMA: Fake precisa implementar 5 métodos para testar 1 caso de uso');

class RepoFalsoGigante implements PedidoRepository {
  private salvos: Pedido[] = [];
  
  salvar(p: Pedido) { this.salvos.push(p); }
  buscarPorId() { return null; }
  listar() { return []; }
  excluir() {}
  exportarCsv() { return ''; }
}

const fakeGigante = new RepoFalsoGigante();
const finalizarPedido = new FinalizarPedido(fakeGigante);
const pedido1 = criarPedidoExemplo('1');
finalizarPedido.executar(pedido1);
console.log('   ✗ Fake tem 5 métodos, mas FinalizarPedido só usa 1 (salvar)');
console.log('   ✗ Métodos irrelevantes: buscarPorId, listar, excluir, exportarCsv\n');

// ============================================
// PROBLEMA 2: Réplica lança exceção (violação LSP)
// ============================================
console.log('2. PROBLEMA: Réplica obrigada a lançar NotImplementedException');

const replica = new PedidoRepositoryReplica();
const pedidosParaReplica = [criarPedidoExemplo('2')];
replica.sincronizar(pedidosParaReplica);

try {
  replica.salvar(criarPedidoExemplo('3'));
  console.log('   ✗ ERRO: deveria ter lançado exceção!');
} catch (e) {
  console.log(`   ✗ Violação LSP: ${(e as Error).message}`);
}

try {
  replica.excluir('2');
  console.log('   ✗ ERRO: deveria ter lançado exceção!');
} catch (e) {
  console.log(`   ✗ Violação LSP: ${(e as Error).message}`);
}
console.log('   ✗ Réplica não pode ser usada onde se espera PedidoRepository completo\n');

// ============================================
// PROBLEMA 3: RelatorioFinanceiro tem acesso a excluir() - PERIGOSO!
// ============================================
console.log('3. PROBLEMA: RelatorioFinanceiro tem acesso a métodos de escrita');

const mysql = new MySqlPedidoRepository();
mysql.salvar(criarPedidoExemplo('4'));
mysql.salvar(criarPedidoExemplo('5'));

const relatorio = new RelatorioFinanceiro(mysql);
// PERIGO: relatorio.repo.excluir('4') - COMPILA E FUNCIONA, mas é um bug!
console.log('   ✗ RelatorioFinanceiro PODE chamar repo.excluir() - risco de exclusão acidental');
console.log('   ✗ RelatorioFinanceiro PODE chamar repo.salvar() - violação de responsabilidade');
console.log('   ✗ RelatorioFinanceiro PODE chamar repo.exportarCsv() - desnecessário\n');

// ============================================
// PROBLEMA 4: Mudança em exportarCsv afeta TODOS os clientes
// ============================================
console.log('4. PROBLEMA: Mudança na interface gorda afeta TODOS os clientes');

console.log('   ✗ Se exportarCsv() mudar de assinatura:');
console.log('     - FinalizarPedido precisa recompilar (não usa exportarCsv)');
console.log('     - CancelarPedido precisa recompilar (não usa exportarCsv)');
console.log('     - ConsultarPedido precisa recompilar (não usa exportarCsv)');
console.log('     - RelatorioFinanceiro precisa recompilar (não usa exportarCsv)');
console.log('     - PedidoRepositoryReplica precisa mudar (não usa exportarCsv de forma significativa)');
console.log('     - MySqlPedidoRepository precisa mudar');
console.log('   ✗ Acoplamento desnecessário entre clientes não relacionados\n');

// ============================================
// PROBLEMA 5: Interface nomeada por tecnologia, não por papel
// ============================================
console.log('5. PROBLEMA: Interface nomeada como "Repository" (tecnologia) e não por papel');
console.log('   ✗ PedidoRepository não revela o que cada cliente precisa');
console.log('   ✗ Dificulta entender as dependências reais\n');

console.log('=== PROBLEMAS DEMONSTRADOS ===');