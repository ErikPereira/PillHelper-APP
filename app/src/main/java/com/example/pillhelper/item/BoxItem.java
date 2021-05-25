package com.example.pillhelper.item;

public class BoxItem {
    private String uuidBox;
    private String nome;

    public BoxItem(String uuidBox, String nome) {
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
