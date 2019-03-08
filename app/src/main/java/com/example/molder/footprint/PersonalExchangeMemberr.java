package com.example.molder.footprint;

public class PersonalExchangeMemberr {

    private int id;
    private String productName;
    private String productPoint;

    public PersonalExchangeMemberr(int id, String productName, String productPoint) {
        this.id = id;
        this.productName = productName;
        this.productPoint = productPoint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPoint() {
        return productPoint;
    }

    public void setProductPoint(String productPoint) {
        this.productPoint = productPoint;
    }
}

