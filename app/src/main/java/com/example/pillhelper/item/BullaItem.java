package com.example.pillhelper.item;

import java.util.ArrayList;

public class BullaItem {

    private String nameBulla;
    private ArrayList<String> title;
    private ArrayList<String> description;
    private ArrayList<String> information;

    public BullaItem(String nameBulla, ArrayList<String> title, ArrayList<String> description,
                     ArrayList<String> information) {
        this.nameBulla = nameBulla;
        this.title = title;
        this.description = description;
        this.information = information;
    }

    public String getNameBulla() {
        return nameBulla;
    }

    public ArrayList<String> getTitle() {
        return title;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public ArrayList<String> getInformation() {
        return information;
    }

    public void setNameBulla(String nameBulla) {
        this.nameBulla = nameBulla;
    }

    public void setTitle(ArrayList<String> title) {
        this.title = title;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public void setInformation(ArrayList<String> information) {
        this.information = information;
    }
}
