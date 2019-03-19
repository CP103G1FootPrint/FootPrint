package com.example.molder.footprint.HomeStroke;

import java.io.Serializable;

public class HomeStrokeFragment_stroke implements Serializable {
    private int tripID;
    private String title;
    private String date;
    private String type;
    private String createID;
    private int days;


    public HomeStrokeFragment_stroke(int tripID, String title, String date, String type, String createID,int days) {
        this.tripID = tripID;
        this.title = title;
        this.date = date;
        this.type = type;
        this.createID = createID;
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