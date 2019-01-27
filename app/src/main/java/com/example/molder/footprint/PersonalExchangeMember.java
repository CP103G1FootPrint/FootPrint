package com.example.molder.footprint;

public class PersonalExchangeMember {
    private int id; // 會員編號
    private int image; // 會員照片
    private int piggy;
    private int point;


    public PersonalExchangeMember() {
        super();
    }

    public PersonalExchangeMember(int id, int image, int point, int piggy) {
        super();
        this.id = id;
        this.image = image;
        this.piggy = piggy;
        this.point = point;
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



    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPiggy() {
        return piggy;
    }

    public void setPiggy(int piggy) {
        this.piggy = piggy;
    }

}

