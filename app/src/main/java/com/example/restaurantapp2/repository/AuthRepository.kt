package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.network.RetrofitClient
import retrofit2.http.Body

class AuthRepository {
    private val api = RetrofitClient.api

    suspend fun login(credentials: Map<String, String>): ApiResponse<Map<String, String>>{
        return api.login(credentials)
    }

    suspend fun localRegister(userInfo: Map<String, String>): ApiResponse<Map<String, String>>{
        return api.register(userInfo)
    }

    suspend fun googleAuth(data: Map<String, String>): ApiResponse<Map<String, String>>{
        return api.googleAuthentication(data)
    }
}