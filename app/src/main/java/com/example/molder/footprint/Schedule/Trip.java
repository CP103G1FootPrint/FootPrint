package com.example.molder.footprint.Schedule;

import java.io.Serializable;

public class Trip implements Serializable {
    private int tripID;
    private String title;
    private String date;
    private String type;
    private String createID;
    private int days;


    public Trip(String title, String date, String type, String createID, int days) {
        this.title = title;
        this.date = date;
        this.type = type;
        this.createID = createID;
        this.days = days;
    }

    public Trip(int tripID, String title, String date, String type) {
        this.tripID = tripID;
        this.title = title;
        this.date = date;
        this.type = type;

    }

    public Trip(int tripID, String title, String date, String type, int days) {
        this.tripID = tripID;
        this.title = title;
        this.date = date;
        this.type = type;
        this.days = days;
    }

    public Trip(String title, String date, String type, int days) {
        this.title = title;
        this.date = date;
        this.type = type;
        this.days = days;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateID() {
        return createID;
    }

    public void setCreateID(String createID) {
        this.createID = createID;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
