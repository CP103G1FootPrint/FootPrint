package com.example.molder.footprint.HomeNews;

import java.io.Serializable;

public class HomeNewsFragment_News_Likes implements Serializable {
        private int likesId;
        private String userId;
        private int imageId;

    public HomeNewsFragment_News_Likes(int likesId, String userId, int imageId) {
        this.likesId = likesId;
        this.userId = userId;
        this.imageId = imageId;
    }
    public HomeNewsFragment_News_Likes(String userId, int imageId) {
        this.userId = userId;
        this.imageId = imageId;
    }

    public HomeNewsFragment_News_Likes(int imageId) {
        this.imageId = imageId;
    }

    public int getLikesId() {
        return likesId;
    }

    public void setLikesId(int likesId) {
        this.likesId = likesId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}




