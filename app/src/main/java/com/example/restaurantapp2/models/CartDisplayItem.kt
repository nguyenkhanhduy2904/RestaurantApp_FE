package com.example.restaurantapp2.models

data class CartDisplayItem(
    val productId: Int,
    val userId :Int,
    val quantity: Int,
    val note : String?,
    val productName: String,
    val unitPrice: Double,
    val totalPrice: Double,
    val imageUrl: String,
    val discountPercent : Int
) {
}