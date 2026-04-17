package com.example.restaurantapp2.models

data class ApiResponse<T>(
    val data: T?,
    val errorMessage: String?
)