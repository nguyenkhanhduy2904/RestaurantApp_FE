package com.example.restaurantapp2.repository

import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.models.ProductRequest
import com.example.restaurantapp2.models.ProductResponse
import com.example.restaurantapp2.network.RetrofitClient

class ProductRepository {

    private val api = RetrofitClient.api

    suspend fun getProducts(): List<Product> {
        return api.getProducts()
    }

    suspend fun createProduct(product: ProductRequest): ProductRequest {
        return api.createProduct(product)
    }

    suspend fun getProductById(productId: Int): Product {
        return api.getProductsById(productId)

    }

    suspend fun updateProduct(productId: Int, product: ProductRequest): Result<ProductResponse> {
        return try {
            val response = api.updateProduct(productId, product)

            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    fun getProducts(): List<Product> {
//        return listOf(
//            Product(1, "Pizza", 20000.0, "A traditional Italian Pizza", 1, "url", 0.0),
//            Product(2, "Burger", 15000.0, "A juicy burger", 1, "url", 0.2),
//            Product(3, "Sushi", 35000.0, "Fresh sushi", 1, "url", 0.1)
//        )
//    }

}