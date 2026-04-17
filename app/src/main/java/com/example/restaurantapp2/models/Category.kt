package com.example.restaurantapp2.models

data class Category(
    val categoryId: Int,
    val categoryName: String,
    val status: String




){
    override fun toString(): String {
        return categoryName.toString()
    }
}
