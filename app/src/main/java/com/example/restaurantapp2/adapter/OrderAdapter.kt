package com.example.restaurantapp2.adapter

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.Utils.convertedDateTime
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.databinding.ItemOrderBinding
import com.example.restaurantapp2.models.OrderResponse

class OrderAdapter(
//    private val orders : List<OrderResponse>
    private val onViewDetail : (OrderResponse) -> Unit
) :RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val orders = mutableListOf<OrderResponse>()

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(item: OrderResponse) {
            binding.tvOrderID.text = item.orderId.toString()
            binding.tvOrderStatus.text = item.orderStatus
            binding.tvPaymentStatus.text = item.paymentStatus
            binding.tvDateTime.text =
                item.createAtParsed?.let { convertedDateTime(it) } ?: "Unknown"
            binding.tvFinalPrice.text = convertedPrice(item.finalPrice)
            binding.tvPaymentMethod.text = item.paymentMethod

            binding.tvViewDetail.setOnClickListener{
                onViewDetail(item)
            }

            if (item.totalPrice == item.finalPrice) {
                binding.tvTotalPrice.visibility = View.GONE
                binding.tvTotalPrice.paintFlags =
                    binding.tvTotalPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            } else {
                binding.tvTotalPrice.visibility = View.VISIBLE
                binding.tvTotalPrice.text = convertedPrice(item.totalPrice)

                binding.tvTotalPrice.paintFlags =
                    binding.tvTotalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val binding = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return OrderViewHolder(binding)


        }

        override fun getItemCount(): Int {
            return orders.size
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            Log.d("DEBUG", "Binding position = $position")
            val item = orders[position]
            holder.bind(item)
        }

    fun updateData(newOrders: List<OrderResponse>) {
        Log.d("OrderAdapter", "updating with ${newOrders.size} orders")

        orders.clear()
        orders.addAll(newOrders)

        notifyDataSetChanged()
    }
}