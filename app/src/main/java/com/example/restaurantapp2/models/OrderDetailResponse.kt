package com.example.restaurantapp2.models

data class OrderDetailResponse(
    val productId: Int,
    val productName: String,
    val unitPrice : Double,
    val quantity: Int,
    val discountPercent: Int,
    val note: String?
) {
}