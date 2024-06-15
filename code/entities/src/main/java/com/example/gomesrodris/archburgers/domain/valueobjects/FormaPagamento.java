package com.example.gomesrodris.archburgers.domain.valueobjects;

public enum FormaPagamento {
    DINHEIRO("Pagamento em dinheiro direto ao caixa");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public static FormaPagamento fromName(String formaPagamento) {
        if (formaPagamento == null) {
            return null;
        }

        for (FormaPagamento value : values()) {
            if (value.name().equalsIgnoreCase(formaPagamento))
                return value;
        }

        throw new IllegalArgumentException("Forma de pagamento inválida: " + formaPagamento);
    }

    public String getDescricao() {
        return descricao;
    }
}