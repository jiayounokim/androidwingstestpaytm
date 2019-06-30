package com.example.wingstestpaytm.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MealModel {
    @SerializedName("meals")
    @Expose
    private ArrayList<MealModel> mealList;
    private String id, name, short_description, image;
    private Float price;

    public MealModel(String id, String name, String short_description, String image, Float price) {
        this.id = id;
        this.name = name;
        this.short_description = short_description;
        this.image = image;
        this.price = price;
    }
    public ArrayList<MealModel> getMealList() {
        return mealList;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getImage() {
        return image;
    }

    public Float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "MealModel{" +
                "mealList=" + mealList +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", short_description='" + short_description + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                '}';
    }
}