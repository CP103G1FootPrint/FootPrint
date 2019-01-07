package com.example.molder.footprint;

public class Trip {
    private int imageid ;
    private String title ;
    private String date ;


    public Trip(int imageid, String title, String date) {
        this.imageid = imageid;
        this.title = title;
        this.date = date;
    }

    public int getImageid() {
        return imageid;
    }

    public void setImageid(int imageid) {
        this.imageid = imageid;
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
}
