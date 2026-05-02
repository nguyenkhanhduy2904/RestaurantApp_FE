package com.example.restaurantapp2.network

import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.Category
import com.example.restaurantapp2.models.CategoryRequest
import com.example.restaurantapp2.models.ChangePasswordRequest
import com.example.restaurantapp2.models.DeviceTokenRequest
import com.example.restaurantapp2.models.OrderRequest
import com.example.restaurantapp2.models.OrderResponse
import com.example.restaurantapp2.models.OrderStatusRequest
import com.example.restaurantapp2.models.OtpResponse
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.models.ProductRequest
import com.example.restaurantapp2.models.ProductResponse
import com.example.restaurantapp2.models.ResetPasswordRequest
import com.example.restaurantapp2.models.UserProfile
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
    suspend fun login(@Body credentials: Map<String, String>): ApiResponse<UserProfile>
    @POST("auth/local/admin-create")
    suspend fun createAdmin(@Body userInfo: Map<String, String>): ApiResponse<UserProfile>

    @POST("auth/local/signup")
    suspend fun register(@Body userInfo: Map<String, String>): ApiResponse<UserProfile>

    @POST("auth/google")
    suspend fun googleAuthentication(@Body data : Map<String, String>) :ApiResponse<UserProfile>

    @GET("user-profile")
    suspend fun getAllUser() : ApiResponse<List<UserProfile>>

    @GET("user-profile/{userId}")
    suspend fun getUserInfo(@Path("userId") userId : Int) : ApiResponse<UserProfile>

    @PUT("user-profile/{userId}")
    suspend fun updateUserInfo(@Path("userId") userId :Int, @Body userProfile: UserProfile): ApiResponse<UserProfile>

    @GET("order/user-order/{id}")
    suspend fun getOrdersByUserId(@Path("id") orderId: Int) : ApiResponse<List<OrderResponse>>

    @PUT("order")
    suspend fun updateOrderStatus(@Body orderStatusRequest : OrderStatusRequest) : ApiResponse<OrderResponse>

    @GET("order")
    suspend fun getAllOrders() : ApiResponse<List<OrderResponse>>

    @POST("order")
    suspend fun placeOrder(@Body orderRequest: OrderRequest): ApiResponse<OrderResponse>

    @POST("token")
    suspend fun registerToken(@Body deviceToken : DeviceTokenRequest): Response<Unit>

    @DELETE("device-token")
    suspend fun deleteToken(
        @Query("token") token: String
    ): Response<Unit>

    @POST("auth/local/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest) : ApiResponse<String>

    @GET("order/vnpay-url/{orderId}")
    suspend fun getVnPayUrl(
        @Path("orderId") orderId: Int
    ): ResponseBody


    @POST("reset-password")
    suspend fun forgotPassword(
        @Query("email") email: String
    ): ApiResponse<String>

    @POST("reset-password/verify-otp")
    suspend fun verifyOtp(
        @Query("otp") otp : String
    ): ApiResponse<OtpResponse>

    @POST("reset-password/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ApiResponse<String>

}