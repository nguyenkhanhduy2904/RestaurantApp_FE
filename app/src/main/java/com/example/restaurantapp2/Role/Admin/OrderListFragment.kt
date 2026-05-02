package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Role.Customer.OrderDetailBottomSheetFragment
import com.example.restaurantapp2.adapter.OrderAdapter
import com.example.restaurantapp2.viewmodels.OrderVM
import com.example.restaurantapp2.viewmodels.UserVM

class OrderListFragment: Fragment(R.layout.fragment_order_list){

//    private val userVM: UserVM by activityViewModels()
    private val orderVM : OrderVM by activityViewModels()
    private lateinit var adapter: OrderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = OrderAdapter(
            onViewDetail = { orderResponse ->
                Log.d("DEBUG", "Clicked order with ${orderResponse.orderDetailResponseList.size} items")

                val bottomSheet = OrderDetailBottomSheetFragmentAdmin(
                    orderResponse
                )

                bottomSheet.show(parentFragmentManager, "OrderDetail")
            }
        )
        Log.d("DEBUG", "Fragment OrderVM = $orderVM")


        val rv = view.findViewById<RecyclerView>(R.id.rvOrderList)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter

        // Observe orders ONLY ONCE

        orderVM.loadAllOrder()

        orderVM.orders.observe(viewLifecycleOwner) { list ->
            Log.d("UI", "Orders received = ${list.size}")
            adapter.updateData(list)
        }

        // Load only when user is ready, but prevent multiple reloads
//        userVM.user.value?.let { user ->
//            orderVM.loadAllOrderByUserId(user.userId)
//        }
//
//        userVM.user.observe(viewLifecycleOwner) { user ->
//            user?.let {
//                orderVM.loadAllOrderByUserId(it.userId)
//            }
//        }


    }

}