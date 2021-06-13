package com.example.pillhelper.item;

public class ClinicalDataItem {
    private String name;
    private String value;

    public ClinicalDataItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}
