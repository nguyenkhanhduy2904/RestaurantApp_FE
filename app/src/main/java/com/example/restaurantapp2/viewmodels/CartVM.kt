package com.example.restaurantapp2.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp2.models.CartItem
import com.example.restaurantapp2.models.CartUI
import com.example.restaurantapp2.models.OrderDetailRequest
import com.example.restaurantapp2.models.OrderRequest
import com.example.restaurantapp2.models.OrderResponse
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.repository.CartRepository
import com.example.restaurantapp2.repository.OrderRepository
import kotlinx.coroutines.launch

class CartVM(
    private val cartRepo :CartRepository,
    private val orderRepo : OrderRepository
): ViewModel() {

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice



    private val _success = MutableLiveData<OrderResponse?>()
    val success: LiveData<OrderResponse?> = _success

    private val _orderResult = MutableLiveData<OrderResponse>()
    val orderResult: LiveData<OrderResponse> = _orderResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    val cartUIList = MediatorLiveData<List<CartUI>>()
    private var cartSource: LiveData<List<CartItem>>? = null
    private var productSource: LiveData<List<Product>>? = null

    fun init(cart: LiveData<List<CartItem>>, product: LiveData<List<Product>>) {

        // already initialized → do nothing
        if (cartSource != null || productSource != null) return

        cartSource = cart
        productSource = product

        cartUIList.addSource(cart) { carts ->
            val products = product.value ?: emptyList()
            buildUI(carts, products)
        }

        cartUIList.addSource(product) { products ->
            val carts = cart.value ?: emptyList()
            buildUI(carts, products)
        }
    }

    private fun buildUI(
        carts: List<CartItem>,
        products: List<Product>
    ) {
        val map = products.associateBy { it.productId }

        cartUIList.value = carts.map {
            CartUI(
                cart = it,
                product = map[it.productId]
            )
        }

        var total = 0.0

        carts.forEach { cart ->
            val product = map[cart.productId]
            if (product != null) {
                val price = product.finalPrice

                total += price * cart.quantity
            }
        }
        _totalPrice.value = total


    }

    fun getCart(userId: Int): LiveData<List<CartItem>> {
        return cartRepo.getCartByUserId(userId)
    }

    fun insert(cart: CartItem) {
        viewModelScope.launch {
            cartRepo.insertOrMerge(cart)
        }
    }
    fun clearCart(userId: Int) {
        viewModelScope.launch {
            cartRepo.clearCart(userId)
        }
    }

    fun delete(cart: CartItem) {
        viewModelScope.launch {
            cartRepo.delete(cart)
        }
    }
    fun placeOrder(userId: Int, address : String, phone: String, paymentStatus: String, paymentMethod: String) = viewModelScope.launch {
        try {
            val cartItems = cartRepo.getCartItems(userId) // suspend List<>

            val details = cartItems.map {
                OrderDetailRequest(
                    productId = it.productId,
                    quantity = it.quantity,
                    note = it.note
                )
            }

            val request = OrderRequest(
                userId = userId,
                address = address,
                phone = phone,
                orderStatus = "PENDING",
                paymentMethod = paymentMethod,
                paymentStatus = paymentStatus,
                orderDetailRequests = details
            )
            Log.d("sending order", "placeOrder: $request")

            val response = orderRepo.placeOrder(request)

            if (response.errorMessage != null) {
                _error.postValue(response.errorMessage)
            } else {
                _success.postValue(response.data)
                Log.d("ORDER", "success emitted")
                clearCart(userId)

            }

        } catch (e: Exception) {
            _error.postValue(e.message ?: "Unknown error")
        }
    }

    fun resetSuccess() {
        _success.postValue(null)
    }

    fun resetError() {
        _error.postValue(null)
    }

    fun increaseQuantity(item: CartItem) = viewModelScope.launch {
        val updated = item.copy(quantity = item.quantity + 1)
        cartRepo.update(updated)
    }

    fun updateNote(item: CartItem, note: String) = viewModelScope.launch {
        val updated = item.copy(note = note)
        cartRepo.update(updated)
    }

    fun decreaseQuantity(item: CartItem) = viewModelScope.launch {
        if (item.quantity > 1) {
            val updated = item.copy(quantity = item.quantity - 1)
            cartRepo.update(updated)
        } else {
            cartRepo.delete(item)
        }
    }

    private val _vnpayUrl = MutableLiveData<String>()
    val vnpayUrl: LiveData<String> = _vnpayUrl

    fun getVnpayUrl(orderId: Int) = viewModelScope.launch {
        try {
            Log.d("VNPay", "STEP 1: calling API")

            val url = orderRepo.getVnPayURL(orderId)

            Log.d("VNPay", "STEP 2: response = $url")

            _vnpayUrl.postValue(url)

        } catch (e: Exception) {
            Log.e("VNPay", "ERROR: ${e.message}", e)
        }
    }
}