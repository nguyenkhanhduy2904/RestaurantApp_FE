package com.example.restaurantapp2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductVM :ViewModel() {
    private val repo = ProductRepository()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products          // public ✅

    init{
        loadProducts()

    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = repo.getProducts()
            } catch (e: Exception) {
                Log.e("ProductVM", "error: ${e.message}")
            }
        }
    }
}