package com.example.restaurantapp2.network

import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.Category
import com.example.restaurantapp2.models.CategoryRequest
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.models.ProductRequest
import com.example.restaurantapp2.models.ProductResponse
import com.example.restaurantapp2.models.UserProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("product")
    suspend fun getProducts() : List<Product>


    @POST("product")
    suspend fun createProduct(@Body product: ProductRequest): ProductRequest

    @PUT("product/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Body product: ProductRequest
    ): Response<ProductResponse>

    @GET("product/{id}")
    suspend fun getProductsById(
        @Path("id") productId: Int
    ): Product





    @GET("category")
    suspend fun getCategories() : List<Category>

    @POST("category")
    suspend fun createCategory(@Body category: CategoryRequest): CategoryRequest


    @POST("auth/local")
    suspend fun login(@Body credentials: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("auth/local/signup")
    suspend fun register(@Body userInfo: Map<String, String>): ApiResponse<Map<String, String>>

    @POST("auth/google")
    suspend fun googleAuthentication(@Body data : Map<String, String>) :ApiResponse<Map<String, String>>


    @GET("user-profile/{userId}")
    suspend fun getUserInfo(@Path("id") userId : Int) : UserProfile

    @PUT("user-profile/{userId}")
    suspend fun updateUserInfo(@Path("id") userId :Int, @Body userProfile: UserProfile): UserProfile

}