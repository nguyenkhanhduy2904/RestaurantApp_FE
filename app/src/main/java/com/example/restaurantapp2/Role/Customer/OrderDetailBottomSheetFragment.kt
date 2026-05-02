package com.example.restaurantapp2.Role.Customer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.convertedDateTime
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.adapter.OrderDetailAdapter
import com.example.restaurantapp2.models.OrderResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderDetailBottomSheetFragment(
    private val orderData : OrderResponse
) : BottomSheetDialogFragment(R.layout.bottom_sheet_order_detail) {


    private lateinit var adapter: OrderDetailAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderDetailAdapter()

        val rv = view.findViewById<RecyclerView>(R.id.rvOrderDetail)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        Log.d("DEBUG", "BottomSheet received ${orderData.orderDetailResponseList.size} items")
        adapter.updateData(orderData.orderDetailResponseList)

        val orderId = view.findViewById<TextView>(R.id.tvUserID)
        val time = view.findViewById<TextView>(R.id.tvDateTime)
        val orderStatus = view.findViewById<TextView>(R.id.tvOrderStatus)
        val paymentStatus = view.findViewById<TextView>(R.id.tvRole)
        val finalPrice = view.findViewById<TextView>(R.id.tvFinalPrice)
        val totalPrice = view.findViewById<TextView>(R.id.tvTotalPrice)
        val paymentMethod = view.findViewById<TextView>(R.id.tvUserEmail)

        orderId.text = orderData.orderId.toString()
        time.text = orderData.createAtParsed?.let { convertedDateTime(it) }
        orderStatus.text = orderData.orderStatus
        paymentMethod.text= orderData.paymentMethod
        paymentStatus.text = orderData.paymentStatus
        finalPrice.text = convertedPrice(orderData.finalPrice)

        if(orderData.totalPrice != orderData.finalPrice){
            totalPrice.visibility = View.VISIBLE
            totalPrice.text = convertedPrice(orderData.totalPrice)
        }
        else{
            totalPrice.visibility = View.GONE
        }





    }
}