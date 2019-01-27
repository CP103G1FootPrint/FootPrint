package com.example.molder.footprint.CheckInShare;

public class Picture {
    private int imageID;
    private String description;
    private String openState;
    private String userID;
    private int landMarkID;

    public Picture() {

    }

    public Picture(int imageID, String description, String openState, String userID, int landMarkID) {
        super();
        this.imageID = imageID;
        this.description = description;
        this.openState = openState;
        this.userID = userID;
        this.landMarkID = landMarkID;
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
}
