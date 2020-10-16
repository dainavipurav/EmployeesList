package com.example.employeeslist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<EmployeesDetailsModel> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EmployeesDetailsModel> getData() {
        return data;
    }

    public void setData(List<EmployeesDetailsModel> data) {
        this.data = data;
    }

}
