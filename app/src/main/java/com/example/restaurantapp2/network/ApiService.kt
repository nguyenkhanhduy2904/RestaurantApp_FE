package com.example.restaurantapp2.network

import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.models.ProductRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("product")
    suspend fun getProducts() : List<Product>


    @POST("product")
    suspend fun createProduct(@Body product: ProductRequest): ProductRequest

    @GET("product/{id}")
    suspend fun getProductsById(
        @Path("id") productId: Int
    ): Product
}