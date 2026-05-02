package com.example.restaurantapp2.models

data class ChangePasswordRequest(
    val userNameLogin : String,
    val oldPass : String,
    val newPass : String
)
