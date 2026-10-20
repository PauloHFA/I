package com.solid.isp.after.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade Item do Pedido
 */
public class Item {
    private final String id;
    private final String nome;
    private final BigDecimal preco;
    private final int quantidade;

    public Item(String id, String nome, BigDecimal preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }

    public BigDecimal subtotal() {
        return preco.multiply(BigDecimal.valueOf(quantidade));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Item{id='" + id + "', nome='" + nome + "', preco=" + preco + ", quantidade=" + quantidade + "}";
    }
}