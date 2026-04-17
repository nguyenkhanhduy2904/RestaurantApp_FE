package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.UserProfile
import com.example.restaurantapp2.network.RetrofitClient
import retrofit2.Retrofit

class UserProfileRepository(){
    private val api = RetrofitClient.api

    suspend fun getUserInfo(userId : Int) : UserProfile{
        return api.getUserInfo(userId)
    }

    suspend fun updateUserInfo (userId : Int, userProfile: UserProfile) : UserProfile{
        return api.updateUserInfo(userId, userProfile)
    }
}
