package com.example.pillhelper;

public class CaixaItem {
    private String idCaixa;
    private String nome;

    public CaixaItem(String idCaixa, String nome) {
        this.idCaixa = idCaixa;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getIdCaixa() {
        return idCaixa;
    }
}
