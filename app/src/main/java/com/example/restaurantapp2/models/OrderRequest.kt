package com.example.restaurantapp2.models

data class OrderRequest(
    val address : String,
    val phone : String,
    val orderStatus : String,
    val paymentStatus: String,
    val paymentMethod: String,
    val userId: Int,

    val orderDetailRequests : List<OrderDetailRequest>
) {
}