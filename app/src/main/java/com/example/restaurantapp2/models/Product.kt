package com.example.restaurantapp2.models

data class Product(
    val productId : Int,
    val productName : String,
    val productPrice : Double,
    val productDescription : String,
    val categoryId : Int,
    val productThumbnailUrl : String,
    val productPriceReduction : Double,
    val status : String
) {
    val isDiscounted : Boolean
//        get() = productPriceReduction >0.0
        get() = false
    val finalPrice : Double
//        get() = productPrice * (1.0 - productPriceReduction)
        get() = productPrice

}