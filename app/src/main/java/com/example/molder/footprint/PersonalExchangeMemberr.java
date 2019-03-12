package com.example.molder.footprint;

public class PersonalExchangeMemberr {

    private int id;
    private String productId;
    private String point;
    private String description;

    public PersonalExchangeMemberr(int id,  String productId, String point, String description
    ) {
        super();
        this.id = id;
        this.productId = productId;
        this.point = point;
        this.description = description;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
