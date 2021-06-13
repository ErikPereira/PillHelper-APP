package com.example.pillhelper.item;

public class BoxItem {
    private String uuidBox;
    private String name;

    public BoxItem(String uuidBox, String name) {
        this.uuidBox = uuidBox;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUuidBox() {
        return uuidBox;
    }
}
