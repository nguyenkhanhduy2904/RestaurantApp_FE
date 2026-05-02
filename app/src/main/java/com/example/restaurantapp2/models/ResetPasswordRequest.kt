package com.example.restaurantapp2.models

data class ResetPasswordRequest(
    val userId : Int,
    val newPassword : String
) {
}