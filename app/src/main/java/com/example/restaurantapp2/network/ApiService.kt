package com.example.restaurantapp2.network

import com.example.restaurantapp2.models.Product
import retrofit2.http.GET

interface ApiService {

    @GET("product")
    suspend fun getProducts() : List<Product>
}