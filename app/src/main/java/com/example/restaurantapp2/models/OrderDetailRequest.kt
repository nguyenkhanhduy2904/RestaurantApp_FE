package com.example.restaurantapp2.models

data class OrderDetailRequest(
    val productId : Int,
    val quantity : Int,
    val note : String?
) {
}