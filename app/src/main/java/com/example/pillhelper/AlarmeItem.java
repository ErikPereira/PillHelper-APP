package com.example.pillhelper;

public class AlarmeItem {
    private String uuidAlarm;
    private int status;
    private int hora;
    private int minuto;
    private String nome;
    private int notificationId;

    public AlarmeItem(String uuidAlarm, int status, String nome, int hora, int minuto, int notificationId) {
        this.uuidAlarm = uuidAlarm;
        this.status = status;
        this.hora = hora;
        this.minuto = minuto;
        this.nome = nome;
        this.notificationId = notificationId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public String getNome() {
        return nome;
    }

    public String getUuidAlarm() {
        return uuidAlarm;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
