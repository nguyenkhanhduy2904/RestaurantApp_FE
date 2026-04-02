package com.example.restaurantapp2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.models.ProductRequest
import com.example.restaurantapp2.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductVM :ViewModel() {
    private val repo = ProductRepository()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products          // public

    private val _selectedProduct = MutableLiveData<Product>()
    val selectedProduct: LiveData<Product> = _selectedProduct



    private val _createStatus = MutableLiveData<Boolean>()
    val createStatus: LiveData<Boolean> = _createStatus
    init{
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = repo.getProducts()
            } catch (e: Exception) {
                Log.e("ProductVM", "error: ${e.message}")
            }
        }
    }
    fun getProductById(productId: Int) {
        viewModelScope.launch {
            try {
                val product = repo.getProductById(productId)
                _selectedProduct.value = product

                // do something with the product, e.g. post to another LiveData for details screen
            } catch (e: Exception) {
                Log.e("ProductVM", "error: ${e.message}")
            }
        }
    }

    fun createProduct(product: ProductRequest) {
        viewModelScope.launch {
            try {
                repo.createProduct(product)
                _createStatus.postValue(true) //  IMPORTANT
            } catch (e: Exception) {
                _createStatus.postValue(false)
            }
        }
    }
}