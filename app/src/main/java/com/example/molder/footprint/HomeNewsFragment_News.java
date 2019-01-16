package com.example.molder.footprint;

import java.io.Serializable;

public class HomeNewsFragment_News implements Serializable {
    private int profilePictureId;
    private String userName;
    private String landmark;
    private int newsPictureId;

    public HomeNewsFragment_News(int profilePictureId, String userName, String landmark, int newsPictureId) {
        this.profilePictureId = profilePictureId;
        this.userName = userName;
        this.landmark = landmark;
        this.newsPictureId = newsPictureId;
    }

    public int getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(int profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public int getNewsPictureId() {
        return newsPictureId;
    }

    public void setNewsPictureId(int newsPictureId) {
        this.newsPictureId = newsPictureId;
    }
}


