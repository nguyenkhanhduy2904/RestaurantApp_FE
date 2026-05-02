package com.example.restaurantapp2.repository

import androidx.lifecycle.LiveData
import com.example.restaurantapp2.data.CartDAO
import com.example.restaurantapp2.models.CartItem

class CartRepository(private val cartDAO: CartDAO) {


    fun getCartByUserId(userId: Int): LiveData<List<CartItem>> {
        return cartDAO.getCartByUser(userId)
    }
    suspend fun getCartItems(userId: Int): List<CartItem> {
        return cartDAO.getCartByUserId(userId)
    }

//    suspend fun insert(cart: CartItem) {
//        cartDAO.insert(cart)
//    }
    suspend fun insertOrMerge(item: CartItem) {
        val normalizedNote = item.note?.trim()

        val existing = normalizedNote?.let {
            cartDAO.findItem(
                productId = item.productId,
                userId = item.userId,
                note = it
            )
        }

        if (existing != null) {
            val updated = existing.copy(
                quantity = existing.quantity + item.quantity
            )
            cartDAO.update(updated)
        } else {
            cartDAO.insert(item.copy(note = normalizedNote))
        }
    }

    suspend fun delete(cart: CartItem) {
        cartDAO.delete(cart)
    }
    suspend fun update(cart: CartItem) {
        cartDAO.update(cart)
    }
    suspend fun clearCart(userId: Int) {
        cartDAO.clearCart(userId)
    }
}