package com.example.restaurantapp2.models

data class OrderStatusRequest(
    val orderId : Int,
    val userId : Int,
    val paymentStatus : String,
    val orderStatus : String,
    val paymentMethod : String
)
