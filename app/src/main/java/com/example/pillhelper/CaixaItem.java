package com.example.pillhelper;

public class CaixaItem {
    private String uuidBox;
    private String nome;

    public CaixaItem(String uuidBox, String nome) {
        this.uuidBox = uuidBox;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getUuidBox() {
        return uuidBox;
    }
}
