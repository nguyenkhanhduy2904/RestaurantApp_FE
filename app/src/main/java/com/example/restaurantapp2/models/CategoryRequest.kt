package com.example.restaurantapp2.models

data class CategoryRequest(
    private val categoryId: Int? = null, // optional
    private val categoryName: String,
    private val status: String
)
