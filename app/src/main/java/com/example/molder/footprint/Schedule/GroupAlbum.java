package com.example.molder.footprint.Schedule;

import java.io.Serializable;

public class GroupAlbum implements Serializable {
    private int id ;
    private String detail ;


    public GroupAlbum(int id, String detail) {
        this.id = id;

        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
