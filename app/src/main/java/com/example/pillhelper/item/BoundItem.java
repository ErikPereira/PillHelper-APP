package com.example.pillhelper.item;

public class BoundItem {
    private String uuid;
    private String registeredBy;
    private String bond;
    private String name;

    public BoundItem(String uuid, String registeredBy, String bond, String name) {
        this.uuid = uuid;
        this.registeredBy = registeredBy;
        this.bond = bond;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public String getBond() {
        return bond;
    }
}
