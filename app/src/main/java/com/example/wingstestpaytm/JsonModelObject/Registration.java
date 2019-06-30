
package com.example.wingstestpaytm.JsonModelObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Registration {

    @SerializedName("registrations")
    @Expose
    private ArrayList<Registration> registrationArrayListModel;

    @SerializedName("logo")
    @Expose
    private String logo;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public ArrayList<Registration> getRegistrationArrayListModel() {
        return registrationArrayListModel;
    }

    public void setRegistrationArrayListModel(ArrayList<Registration> registrationArrayListModel) {
        this.registrationArrayListModel = registrationArrayListModel;
    }
    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
