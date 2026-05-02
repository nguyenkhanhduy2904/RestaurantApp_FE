package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.convertedDateTime
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.adapter.OrderDetailAdapter
import com.example.restaurantapp2.models.OrderResponse
import com.example.restaurantapp2.models.OrderStatusRequest
import com.example.restaurantapp2.viewmodels.OrderVM
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderDetailBottomSheetFragmentAdmin(
    private val orderData : OrderResponse
) : BottomSheetDialogFragment(R.layout.bottom_sheet_order_detail_admin) {


    private val orderVM : OrderVM by activityViewModels()
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
        val phone = view.findViewById<TextView>(R.id.tvPhone)
        val address = view.findViewById<TextView>(R.id.tvAddress)

        val btnNegative = view.findViewById<Button>(R.id.btnNegative)
        val btnPositive = view.findViewById<Button>(R.id.btnPositive)

        orderId.text = orderData.orderId.toString()
        time.text = orderData.createAtParsed?.let { convertedDateTime(it) }
        orderStatus.text = orderData.orderStatus
        paymentMethod.text= orderData.paymentMethod
        paymentStatus.text = orderData.paymentStatus
        finalPrice.text = convertedPrice(orderData.finalPrice)
        phone.text =orderData.phone
        address.text =orderData.address

        if(orderData.totalPrice != orderData.finalPrice){
            totalPrice.visibility = View.VISIBLE
            totalPrice.text = convertedPrice(orderData.totalPrice)
        }
        else{
            totalPrice.visibility = View.GONE
        }

        val status = orderData.orderStatus
        val payment = orderData.paymentStatus

        when (status) {
            "PENDING" -> {
                btnNegative.visibility = View.VISIBLE
                btnPositive.visibility = View.VISIBLE
                btnPositive.text = "Accept"
            }

            "IN_PROCESS" -> {
                btnNegative.visibility = View.VISIBLE
                btnPositive.visibility = View.VISIBLE
                btnPositive.text = "Delivered!"
            }

            "DELIVERED" -> {
                btnNegative.visibility = View.GONE
                if (payment == "UNPAID") {
                    btnPositive.visibility = View.VISIBLE
                    btnPositive.text = "Receive Money"
                } else {
                    btnPositive.visibility = View.GONE
                }
            }

            else -> {
                btnNegative.visibility = View.GONE
                btnPositive.visibility = View.GONE
            }
        }



        btnNegative.setOnClickListener{
            orderVM.updateOrderStatus(
                OrderStatusRequest(
                    orderId = orderData.orderId,
                    userId = orderData.userId,
                    orderStatus = "CANCELED",
                    paymentMethod = "",
                    paymentStatus = ""
                )
            )
            orderVM.loadAllOrder()
            dismiss()
        }
        btnPositive.setOnClickListener {
            when (orderData.orderStatus) {
                "PENDING" -> {
                    orderVM.updateOrderStatus(
                        OrderStatusRequest(
                            orderId = orderData.orderId,
                            userId = orderData.userId,
                            orderStatus = "IN_PROCESS",
                            paymentMethod = "",
                            paymentStatus = ""
                        )
                    )

                }
                "IN_PROCESS" -> {
                    orderVM.updateOrderStatus(
                        OrderStatusRequest(
                            orderId = orderData.orderId,
                            userId = orderData.userId,
                            orderStatus = "DELIVERED",
                            paymentMethod = "",
                            paymentStatus = ""
                        )
                    )
                }
                "DELIVERED" -> {
                    orderVM.updateOrderStatus(
                        OrderStatusRequest(
                            orderId = orderData.orderId,
                            userId = orderData.userId,
                            orderStatus = "DELIVERED",
                            paymentMethod = orderData.paymentMethod,
                            paymentStatus = "PAID"
                        )
                    )
                }
            }
            orderVM.loadAllOrder()
            dismiss()

        }










    }
}