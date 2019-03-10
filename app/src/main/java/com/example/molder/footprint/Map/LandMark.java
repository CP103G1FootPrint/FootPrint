package com.example.molder.footprint.Map;

import java.io.Serializable;

public class LandMark implements Serializable {

    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String description;
    private String openingHours;
    private String type;
    private int userID;
    private int imageID;
    private String timeStamp;
    private String nickName;
    private String account;
    private double star;
    public LandMark() {

    }

    public LandMark(int id, String name, String address, double latitude, double longitude, String description, String openingHours, String type) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.openingHours = openingHours;
        this.type = type;
    }

    public LandMark(int id, String name, String address, double latitude, double longitude, String description, String openingHours, String type,double star) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.openingHours = openingHours;
        this.type = type;
        this.star = star;
    }

    public LandMark(String account, int imageID, String nickName) {
        super();
        this.account = account;
        this.imageID = imageID;
        this.nickName = nickName;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }
}
