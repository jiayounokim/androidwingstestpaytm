
package com.example.wingstestpaytm.JsonModelObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("registration")
    @Expose
    private Registration registration;
    @SerializedName("driver")
    @Expose
    private Object driver;
    @SerializedName("order_details")
    @Expose
    private List<OrderDetail> orderDetails = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("address")
    @Expose
    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Object getDriver() {
        return driver;
    }

    public void setDriver(Object driver) {
        this.driver = driver;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", registration=" + registration +
                ", driver=" + driver +
                ", orderDetails=" + orderDetails +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
