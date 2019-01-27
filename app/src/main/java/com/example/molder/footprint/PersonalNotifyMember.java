package com.example.molder.footprint;

public class PersonalNotifyMember
{
    private int id; // 會員編號
    private int image; // 會員照片
    private String notify;


    public PersonalNotifyMember() {
        super();
    }

    public PersonalNotifyMember(int id, int image, String notify) {
        super();
        this.id = id;
        this.image = image;
        this.notify = notify;
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

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }
}

