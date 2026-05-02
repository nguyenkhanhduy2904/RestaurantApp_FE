package com.example.restaurantapp2.repository

import android.util.Log
import com.example.restaurantapp2.models.ApiResponse
import com.example.restaurantapp2.models.OrderRequest
import com.example.restaurantapp2.models.OrderResponse
import com.example.restaurantapp2.models.OrderStatusRequest
import com.example.restaurantapp2.network.RetrofitClient

class OrderRepository {

    private val api = RetrofitClient.api

    suspend fun getAllOrdersByUserId(userId: Int): ApiResponse<List<OrderResponse>>{
        Log.d("DEBUG", "Calling API with userId = $userId")
        val result = api.getOrdersByUserId(userId)
        Log.d("DEBUG", "Raw API result = $result")
        return result
    }
    suspend fun getAllOrders(): ApiResponse<List<OrderResponse>>{
        return api.getAllOrders()
    }
    suspend fun placeOrder(orderRequest: OrderRequest): ApiResponse<OrderResponse>{
        return api.placeOrder(orderRequest)
    }

    suspend fun updateOrderStatus(orderStatusRequest: OrderStatusRequest) : ApiResponse<OrderResponse>{
        return api.updateOrderStatus(orderStatusRequest)
    }

    suspend fun getVnPayURL(orderId: Int): String {
        return api.getVnPayUrl(orderId).string()
    }


}