package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.ChangePasswordRequest
import com.example.restaurantapp2.models.OtpResponse
import com.example.restaurantapp2.models.ResetPasswordRequest
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.network.RetrofitClient
import retrofit2.http.Body

class AuthRepository {
    private val api = RetrofitClient.api

    suspend fun login(credentials: Map<String, String>): ApiResponse<UserProfile>{
        return api.login(credentials)
    }

    suspend fun localRegister(userInfo: Map<String, String>): ApiResponse<UserProfile>{
        return api.register(userInfo)
    }

    suspend fun googleAuth(data: Map<String, String>): ApiResponse<UserProfile>{
        return api.googleAuthentication(data)
    }

    suspend fun changePassword(request: ChangePasswordRequest) :ApiResponse<String>{
        return api.changePassword(request);
    }
    suspend fun createAdmin(userInfo: Map<String, String>) :ApiResponse<UserProfile>{
        return api.createAdmin(userInfo)
    }

    suspend fun forgotPassword(email: String): ApiResponse<String> {
        return api.forgotPassword(email)
    }

    suspend fun verifyOtp(otp: String) : ApiResponse<OtpResponse>{
        return api.verifyOtp(otp)
    }

    suspend fun resetPassword(request: ResetPasswordRequest) : ApiResponse<String>{
        return api.resetPassword(request)
    }

}