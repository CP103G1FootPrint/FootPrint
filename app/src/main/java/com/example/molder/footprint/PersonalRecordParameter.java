package com.example.molder.footprint;

public class PersonalRecordParameter
 {
    private int id; // 會員編號
    private int image; // 會員照片


    public PersonalRecordParameter() {
        super();
    }

    public PersonalRecordParameter(int id, int image, String name) {
        super();
        this.id = id;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

