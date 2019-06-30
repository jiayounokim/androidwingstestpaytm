
package com.example.wingstestpaytm.JsonModelObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("meal")
    @Expose
    private Meal meal;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("sub_total")
    @Expose
    private Integer subTotal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Integer subTotal) {
        this.subTotal = subTotal;
    }


    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", meal=" + meal +
                ", quantity=" + quantity +
                ", subTotal=" + subTotal +
                '}';
    }
}
