package com.example.wingstestpaytm.JsonModelObject.revenue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Revenue {

    @SerializedName("Sat")
    @Expose
    private Integer sat;
    @SerializedName("Sun")
    @Expose
    private Integer sun;
    @SerializedName("Tue")
    @Expose
    private Integer tue;
    @SerializedName("Wed")
    @Expose
    private Integer wed;
    @SerializedName("Mon")
    @Expose
    private Integer mon;
    @SerializedName("Thu")
    @Expose
    private Integer thu;
    @SerializedName("Fri")
    @Expose
    private Integer fri;

    public Integer getSat() {
        return sat;
    }

    public void setSat(Integer sat) {
        this.sat = sat;
    }

    public Integer getSun() {
        return sun;
    }

    public void setSun(Integer sun) {
        this.sun = sun;
    }

    public Integer getTue() {
        return tue;
    }

    public void setTue(Integer tue) {
        this.tue = tue;
    }

    public Integer getWed() {
        return wed;
    }

    public void setWed(Integer wed) {
        this.wed = wed;
    }

    public Integer getMon() {
        return mon;
    }

    public void setMon(Integer mon) {
        this.mon = mon;
    }

    public Integer getThu() {
        return thu;
    }

    public void setThu(Integer thu) {
        this.thu = thu;
    }

    public Integer getFri() {
        return fri;
    }

    public void setFri(Integer fri) {
        this.fri = fri;
    }

    @Override
    public String toString() {
        return "Revenue{" +
                "sat=" + sat +
                ", sun=" + sun +
                ", tue=" + tue +
                ", wed=" + wed +
                ", mon=" + mon +
                ", thu=" + thu +
                ", fri=" + fri +
                '}';
    }
}
