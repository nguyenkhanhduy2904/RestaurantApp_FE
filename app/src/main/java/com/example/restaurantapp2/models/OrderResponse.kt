package com.example.restaurantapp2.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class OrderResponse(
    val orderId : Int,
    val address : String,
    val phone : String,
    val orderStatus : String,
    val paymentStatus: String,
    val paymentMethod: String,
    val totalPrice : Double,
    val finalPrice : Double,
    val createAt : String,
    val userId: Int,
    val userName : String,


    val orderDetailResponseList : List<OrderDetailResponse>
) {
    val createAtParsed: LocalDateTime?
        get() = try {
            LocalDateTime.parse(createAt)
        } catch (e: Exception) {
            null
        }

    val displayFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault())
    val createAtDisplay: String?
        get() = try {
            val parsed = LocalDateTime.parse(createAt)
            parsed.format(displayFormatter)
        } catch (e: Exception) {
            null
        }
}