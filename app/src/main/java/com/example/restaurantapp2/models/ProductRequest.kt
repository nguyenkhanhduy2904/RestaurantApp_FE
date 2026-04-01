package com.example.restaurantapp2.models

data class ProductRequest(
    val productId: Int? = null, // optional
    val productName: String,
    val productPrice: Double,
    val productDescription: String,
    val productThumbnailUrl: String,
    val productCategory: Int

)
