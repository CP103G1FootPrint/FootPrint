package com.example.molder.footprint.HomeNews;

import java.io.Serializable;

public class HomeNewsFragment_News_Collection implements Serializable {
    private int collectionId;
    private String userId;
    private int imageId;

    public HomeNewsFragment_News_Collection(int collectionId) {
        super();
        this.collectionId = collectionId;
    }

    public HomeNewsFragment_News_Collection(String userId, int imageId) {
        super();
        this.userId = userId;
        this.imageId = imageId;
    }

    public HomeNewsFragment_News_Collection(int collectionId, String userId, int imageId) {
        super();
        this.collectionId = collectionId;
        this.userId = userId;
        this.imageId = imageId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
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




