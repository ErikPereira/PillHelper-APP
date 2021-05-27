package com.example.pillhelper.item;

public class AlarmItem {
    private String uuidAlarm;
    private int status;
    private int hour;
    private int min;
    private String name;
    private int notificationId;

    public AlarmItem(String uuidAlarm, int status, String name, int hour, int min, int notificationId) {
        this.uuidAlarm = uuidAlarm;
        this.status = status;
        this.hour = hour;
        this.min = min;
        this.name = name;
        this.notificationId = notificationId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getHours() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public String getUuidAlarm() {
        return uuidAlarm;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
