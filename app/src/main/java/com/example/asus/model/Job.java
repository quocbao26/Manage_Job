package com.example.asus.model;

import java.io.Serializable;

public class Job implements Serializable{
    private String id;
    private String name;
    private String content;
    private String location;
    private String date;
    private String hour;
    private int keyPri;
    private boolean status;

    public Job() {
    }

    public Job(String id, String name, String content, String location, String date, String hour, int keyPri, boolean status) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.location = location;
        this.date = date;
        this.hour = hour;
        this.keyPri = keyPri;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getKeyPri() {
        return keyPri;
    }

    public void setKeyPri(int keyPri) {
        this.keyPri = keyPri;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
