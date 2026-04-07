package com.example.restaurantapp2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp2.models.Category
import com.example.restaurantapp2.models.CategoryRequest
import com.example.restaurantapp2.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryVM :ViewModel() {

    private val repo = CategoryRepository()
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories          // public

    private val _selectedCategory = MutableLiveData<Category>()
    val selectedCategory: LiveData<Category> = _selectedCategory

    private val _createStatus = MutableLiveData<Boolean>()
    val createStatus: LiveData<Boolean> = _createStatus

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = repo.getCategories()
                Log.d("CATEGORY_DEBUG", "API result size: ${result.size}")
                Log.d("CATEGORY_DEBUG", "API result: $result")

                _categories.value = result
            } catch (e: Exception) {
                Log.e("CATEGORY_DEBUG", "Error: ${e.message}")
                _errorMessage.value = "Failed to load categories: ${e.message}"
            }
        }
    }

    fun createCategory(category: CategoryRequest) {
        viewModelScope.launch {
            try{
                val result = repo.createCategory(category)

            }catch (e:Exception){
                Log.e("CATEGORY_DEBUG", "Error: ${e.message}")
                _errorMessage.value = "Failed to add categories: ${e.message}"
            }
        }

    }

}