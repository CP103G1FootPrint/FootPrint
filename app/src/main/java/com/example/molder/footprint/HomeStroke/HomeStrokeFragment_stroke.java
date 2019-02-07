package com.example.molder.footprint.HomeStroke;

import java.io.Serializable;

public class HomeStrokeFragment_stroke implements Serializable {
   private int stroke_ImgViewId,stroke_CiUserId;
   private String stroke_TvUserName,stroke_TvTitle,stroke_TvDate;

    public int getStroke_ImgViewId() {
        return stroke_ImgViewId;
    }

    public void setStroke_ImgViewId(int stroke_ImgViewId) {
        this.stroke_ImgViewId = stroke_ImgViewId;
    }

    public int getStroke_CiUserId() {
        return stroke_CiUserId;
    }

    public void setStroke_CiUserId(int stroke_CiUserId) {
        this.stroke_CiUserId = stroke_CiUserId;
    }

    public String getStroke_TvUserName() {
        return stroke_TvUserName;
    }

    public void setStroke_TvUserName(String stroke_TvUserName) {
        this.stroke_TvUserName = stroke_TvUserName;
    }

    public String getStroke_TvTitle() {
        return stroke_TvTitle;
    }

    public void setStroke_TvTitle(String stroke_TvTitle) {
        this.stroke_TvTitle = stroke_TvTitle;
    }

    public String getStroke_TvDate() {
        return stroke_TvDate;
    }

    public void setStroke_TvDate(String stroke_TvDate) {
        this.stroke_TvDate = stroke_TvDate;
    }

    public HomeStrokeFragment_stroke(int stroke_ImgViewId, int stroke_CiUserId, String stroke_TvUserName,
                                     String stroke_TvTitle, String stroke_TvDate) {
        this.stroke_ImgViewId = stroke_ImgViewId;
        this.stroke_CiUserId = stroke_CiUserId;
        this.stroke_TvUserName = stroke_TvUserName;
        this.stroke_TvTitle = stroke_TvTitle;
        this.stroke_TvDate = stroke_TvDate;

    }
}
