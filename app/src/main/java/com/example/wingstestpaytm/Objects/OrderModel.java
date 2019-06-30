package com.example.wingstestpaytm.Objects;

import com.example.wingstestpaytm.JsonModelObject.Customer;
import com.example.wingstestpaytm.JsonModelObject.OrderDetail;
import com.example.wingstestpaytm.JsonModelObject.Registration;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    @SerializedName("id")
    @Expose
    private String id;


    private String restaurantName;


    private String customerName;


    private String customerAddress;


    private String customerImage;

    @SerializedName("orders")
    @Expose
    private ArrayList<OrderModel> orders;

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


    public void setId(String id) {
        this.id = id;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public void setOrders(ArrayList<OrderModel> orders) {
        this.orders = orders;
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

    public ArrayList<OrderModel> getOrders() {
        return orders;
    }

    public OrderModel(String id, String restaurantName, String customerName, String customerAddress, String customerImage) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerImage = customerImage;
    }

    public String getId() {
        return id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "id='" + id + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerImage='" + customerImage + '\'' +
                ", orders=" + orders +
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
