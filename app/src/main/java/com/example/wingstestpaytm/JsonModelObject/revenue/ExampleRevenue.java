package com.example.wingstestpaytm.JsonModelObject.revenue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExampleRevenue {

    @SerializedName("revenue")
    @Expose
    private Revenue revenue;

    public ExampleRevenue(Revenue revenue) {
        this.revenue = revenue;
    }

    public Revenue getRevenue() {
        return revenue;
    }

    public void setRevenue(Revenue revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "ExampleRevenue{" +
                "revenue=" + revenue +
                '}';
    }
}
