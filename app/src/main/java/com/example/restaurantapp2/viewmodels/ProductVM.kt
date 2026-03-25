package com.example.restaurantapp2.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.repository.ProductRepository

class ProductVM :ViewModel() {
    private val repo = ProductRepository()
    val products = MutableLiveData<List<Product>>()

    init{
        products.value = repo.getProducts()
        Log.d("ProductVM", "loaded ${products.value?.size} products")
    }
}