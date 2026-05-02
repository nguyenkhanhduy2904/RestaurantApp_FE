package com.example.restaurantapp2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,

    val productId: Int,
    val userId: Int,
    val quantity: Int,
    val note: String?
)