package com.solid.isp.before.repository;

import com.solid.isp.before.domain.Pedido;
import java.util.List;

/**
 * VERSÃO QUE VIOLA O ISP - Interface "gorda"
 * Uma única interface com todos os métodos, forçando todos os clientes
 * e implementações a lidar com métodos irrelevantes
 */
public interface PedidoRepository {
    void salvar(Pedido pedido);
    Pedido buscarPorId(String id);
    List<Pedido> listar();
    void excluir(String id);
    String exportarCsv();
}