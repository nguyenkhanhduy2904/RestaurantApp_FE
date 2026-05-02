package com.example.restaurantapp2.models

data class DeviceTokenRequest(
    val userId : Int,
    val fcmToken : String
)
