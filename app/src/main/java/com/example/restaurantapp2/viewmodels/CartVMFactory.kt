package com.example.restaurantapp2.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantapp2.repository.CartRepository
import com.example.restaurantapp2.repository.OrderRepository

class CartVMFactory(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository
) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartVM(cartRepo, orderRepo) as T
    }
}