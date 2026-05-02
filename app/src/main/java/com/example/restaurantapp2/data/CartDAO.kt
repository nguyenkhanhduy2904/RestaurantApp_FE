package com.example.restaurantapp2.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.restaurantapp2.models.CartItem
@Dao
interface CartDAO {
    @Query("SELECT * FROM cart WHERE userId = :userId")
    suspend fun getCartByUserId(userId: Int): List<CartItem>

    @Query("SELECT * FROM cart WHERE userId = :userId")
    fun getCartByUser(userId: Int): LiveData<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)

    @Update
    suspend fun update(item: CartItem)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Query("""
    SELECT * FROM cart
    WHERE productId = :productId
    AND userId = :userId
    AND note = :note
    LIMIT 1
    """)
    suspend fun findItem(productId: Int, userId: Int, note: String): CartItem?
}
