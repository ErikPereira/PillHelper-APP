package com.example.pillhelper.item;

public class SupervisorItem {
    private String uuidSupervisor;
    private String registeredBy;
    private String bond;
    private String name;

    public SupervisorItem(String uuidSupervisor, String registeredBy, String bond, String name) {
        this.uuidSupervisor = uuidSupervisor;
        this.registeredBy = registeredBy;
        this.bond = bond;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUuidSupervisor() {
        return uuidSupervisor;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public String getBond() {
        return bond;
    }
}
