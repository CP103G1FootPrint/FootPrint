package com.example.molder.footprint.Personal;

public class PersonalCollectMember {
    private int id; // 會員編號
    private int imageId; // 會員照片


    public PersonalCollectMember() {
        super();
    }

    public PersonalCollectMember(int id, int imageId) {
        super();
        this.id = id;
        this.imageId = imageId;
    }

    public int getImage() {
        return imageId;
    }

    public void setImage(int imageId) {
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}
