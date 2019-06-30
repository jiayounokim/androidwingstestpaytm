package com.example.wingstestpaytm.Objects;

public class RestaurantModel {

    private String id, name, address, logo;




    public RestaurantModel(String id, String name, String address, String logo) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.logo = logo;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLogo() {
        return logo;
    }

    @Override
    public String toString() {
        return "RestaurantModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
