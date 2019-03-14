package com.example.molder.footprint.HomeNews;

import java.io.Serializable;

public class HomeNewsFragment_News implements Serializable {
    private int imageID;
    private String description;
    private String openState;
    private String userID;
    private int landMarkID;
    private String likesCount;
    private int likeId;
    private int collectionId;
    private String nickName;
    private String landMarkName;

    public HomeNewsFragment_News(String likesCount,int imageID) {
        super();
        this.likesCount = likesCount;
        this.imageID = imageID;
    }

    public HomeNewsFragment_News(int imageID, String description, String openState, String userID, int landMarkID,String likesCount) {
        super();
        this.imageID = imageID;
        this.description = description;
        this.openState = openState;
        this.userID = userID;
        this.landMarkID = landMarkID;
        this.likesCount = likesCount;
    }

    public HomeNewsFragment_News(int imageID, String description, String openState,
                                 String userID, int landMarkID,String likesCount,
                                 int likeId, int collectionId ,String nickName, String landMarkName) {
        super();
        this.imageID = imageID;
        this.description = description;
        this.openState = openState;
        this.userID = userID;
        this.landMarkID = landMarkID;
        this.likesCount = likesCount;
        this.likeId = likeId;
        this.collectionId = collectionId;
        this.nickName = nickName;
        this.landMarkName = landMarkName;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpenState() {
        return openState;
    }

    public void setOpenState(String openState) {
        this.openState = openState;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getLandMarkID() {
        return landMarkID;
    }

    public void setLandMarkID(int landMarkID) {
        this.landMarkID = landMarkID;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLandMarkName() {
        return landMarkName;
    }

    public void setLandMarkName(String landMarkName) {
        this.landMarkName = landMarkName;
    }
}


