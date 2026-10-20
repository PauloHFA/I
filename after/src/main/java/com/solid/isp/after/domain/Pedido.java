package com.solid.isp.after.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidade Pedido
 */
public class Pedido {
    private final String id;
    private final Cliente cliente;
    private final LocalDateTime data;
    private Status status;
    private final List<Item> itens;

    public enum Status {
        ABERTO, FINALIZADO, CANCELADO
    }

    public Pedido(String id, Cliente cliente) {
        this(id, cliente, LocalDateTime.now(), Status.ABERTO);
    }

    public Pedido(String id, Cliente cliente, LocalDateTime data, Status status) {
        this.id = id;
        this.cliente = cliente;
        this.data = data;
        this.status = status;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(Item item) {
        itens.add(item);
    }

    public List<Item> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public BigDecimal total() {
        return itens.stream()
                .map(Item::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void finalizar() {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Pedido sem itens não pode ser finalizado");
        }
        this.status = Status.FINALIZADO;
    }

    public void cancelar() {
        if (this.status == Status.FINALIZADO) {
            throw new IllegalStateException("Pedido finalizado não pode ser cancelado");
        }
        this.status = Status.CANCELADO;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getData() { return data; }
    public Status getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Pedido{id='" + id + "', cliente=" + cliente.getNome() + ", data=" + data + ", status=" + status + ", total=" + total() + "}";
    }
}