package com.example.restaurantapp2.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp2.models.OrderResponse
import com.example.restaurantapp2.models.OrderStatusRequest
import com.example.restaurantapp2.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderVM: ViewModel() {

    private val repo = OrderRepository()

    private val _orders = MutableLiveData<List<OrderResponse>>(emptyList())
    val orders: LiveData<List<OrderResponse>> = _orders

    private val _createStatus = MutableLiveData<Boolean>()
    val createStatus: LiveData<Boolean> = _createStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        Log.d("DEBUG", "OrderVM created")
    }

    fun loadAllOrderByUserId(id: Int) {
        viewModelScope.launch {
            try {
                val response = repo.getAllOrdersByUserId(id)

                val list = response.data ?: emptyList()

                Log.d("VM", "Loaded size = ${list.size}")

                _orders.value = list   // 🔥 THIS is correct

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error"
            }
        }
    }

    fun loadAllOrder(){
        viewModelScope.launch {
            try {
                val response = repo.getAllOrders()

                val list = response.data ?: emptyList()

                Log.d("VM", "Loaded size = ${list.size}")

                _orders.value = list


            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error"
            }
        }

    }

    fun updateOrderStatus(orderStatusRequest : OrderStatusRequest) {
        viewModelScope.launch {
            try {
                val response = repo.updateOrderStatus(orderStatusRequest)

                if(response.errorMessage!=null){
                    _errorMessage.value = response.errorMessage.toString()
                }
                if(response.data !=null){
                    loadAllOrder() // refresh AFTER success
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error"
            }
        }
    }


}