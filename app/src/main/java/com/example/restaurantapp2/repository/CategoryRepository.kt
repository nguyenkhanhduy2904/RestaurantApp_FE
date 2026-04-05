package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.Category
import com.example.restaurantapp2.network.RetrofitClient

class CategoryRepository {
    private val api = RetrofitClient.api

    suspend fun getCategories() : List<Category> {
        return api.getCategories()
    }
}