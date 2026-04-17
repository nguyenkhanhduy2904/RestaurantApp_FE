package com.example.restaurantapp2.models

data class CategoryResponse(
    val categoryId: Int? = null, // optional
    val categoryName: String,
    val status: String
)
