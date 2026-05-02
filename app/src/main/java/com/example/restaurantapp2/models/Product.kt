package com.example.restaurantapp2.models

data class Product(
    val productId : Int,
    val productName : String,
    val productPrice : Double,
    val productDescription : String,
    val categoryId : Int,
    val productThumbnailUrl : String,
    val priceReduction : Float,
    val status : String
) {
    val isDiscounted : Boolean
        get() = priceReduction >0.0

    val finalPrice : Double
        get() = productPrice * (1.0 - priceReduction / 100.0)

}