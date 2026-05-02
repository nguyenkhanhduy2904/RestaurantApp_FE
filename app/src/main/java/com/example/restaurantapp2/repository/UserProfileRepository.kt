package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.network.RetrofitClient
import com.google.android.gms.common.api.Api
import retrofit2.Retrofit

class UserProfileRepository(){
    private val api = RetrofitClient.api

    suspend fun getUserInfo(userId : Int) : ApiResponse<UserProfile>{
        return api.getUserInfo(userId)
    }

    suspend fun updateUserInfo (userId : Int, userProfile: UserProfile) : ApiResponse<UserProfile>{
        return api.updateUserInfo(userId, userProfile)
    }
    suspend fun getAllUser() : ApiResponse<List<UserProfile>>{
        return api.getAllUser()
    }
}
