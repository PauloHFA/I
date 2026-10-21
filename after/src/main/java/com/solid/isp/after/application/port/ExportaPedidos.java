package com.solid.isp.after.application.port;

/**
 * Role interface: Exportar pedidos para CSV
 */
public interface ExportaPedidos {
    String exportarCsv();

    String exportarCabecalhoCsv();
}