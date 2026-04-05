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

    var allProducts: List<Product> = emptyList()

    private var selectedCategoryId: Int? = null

    private val repo = ProductRepository()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products          // public

    private val _selectedProduct = MutableLiveData<Product>()
    val selectedProduct: LiveData<Product> = _selectedProduct




    private val _createStatus = MutableLiveData<Boolean>()
    val createStatus: LiveData<Boolean> = _createStatus

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init{
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val data = repo.getProducts()
                allProducts = data          //store original
                _products.value = data      //show all
                allProducts.forEach {
                    Log.d("ProductVM", "Loaded product cate id: ${it.categoryId}")
                }
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

    fun updateProduct(productId: Int, product: ProductRequest) {
        viewModelScope.launch {
            val result = repo.updateProduct(productId, product)

            result.onSuccess {
                _updateStatus.postValue(true)
            }.onFailure { e ->
                _updateStatus.postValue(false)
                _errorMessage.postValue(e.message ?: "Unknown error")
            }
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = false
    }

    fun filterProductsByCategory(categoryId: Int) {

        selectedCategoryId =
            if (selectedCategoryId == categoryId) null  // second click => deselect
            else categoryId                            // first click => select

        val result = selectedCategoryId?.let { id ->
            allProducts.filter { it.categoryId == id }
        } ?: allProducts

        _products.value = result
    }
}