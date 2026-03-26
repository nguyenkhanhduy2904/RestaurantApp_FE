package com.example.restaurantapp2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    private const val BASE_URL = " https://restaurantapp-be.onrender.com/api/v1/"

    val api : ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}