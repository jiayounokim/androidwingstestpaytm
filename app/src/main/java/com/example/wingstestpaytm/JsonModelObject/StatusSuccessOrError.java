package com.example.wingstestpaytm.JsonModelObject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class StatusSuccessOrError {

    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusSuccessOrError{" +
                "status='" + status + '\'' +
                '}';
    }
}