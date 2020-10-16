package com.example.employeeslist.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "http://dummy.restapiexample.com";
    public static Retrofit mRetrofit;

    public static Retrofit getAPI_Client(){
        if (mRetrofit == null){
            mRetrofit =  new Retrofit.Builder().baseUrl(BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).build();
        }
        return mRetrofit;
    }
}
