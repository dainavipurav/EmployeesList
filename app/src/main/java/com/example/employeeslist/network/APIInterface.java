package com.example.employeeslist.network;

import com.example.employeeslist.models.EmployeesDetailsModel;
import com.example.employeeslist.models.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {
    @GET("/api/v1/employees")
    Call<ResponseModel> getResponse();

}
