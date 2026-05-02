package com.example.restaurantapp2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val ONLINE_URL = "https://restaurantapp-be.onrender.com/api/v1/"
    private const val LOCAL_URL = "http://10.0.2.2:8080/api/v1/"
    private const val REAL_DEVICE = "http://192.168.170.203:8080/api/v1/"

    val api : ApiService by lazy {//change the URL to use local host or render host
        Retrofit.Builder().baseUrl(ONLINE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}