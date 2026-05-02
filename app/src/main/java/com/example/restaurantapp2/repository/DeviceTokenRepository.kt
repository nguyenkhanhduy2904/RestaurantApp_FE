package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.DeviceTokenRequest
import com.example.restaurantapp2.network.ApiService
import com.example.restaurantapp2.network.RetrofitClient
import retrofit2.Response

class DeviceTokenRepository {

    private val api = RetrofitClient.api


    suspend fun registerToken(deviceTokenRequest: DeviceTokenRequest) : Response<Unit> {
        return api.registerToken(deviceTokenRequest)
    }
    suspend fun deleteToken(token : String): Response<Unit>{
        return api.deleteToken(token)
    }


}