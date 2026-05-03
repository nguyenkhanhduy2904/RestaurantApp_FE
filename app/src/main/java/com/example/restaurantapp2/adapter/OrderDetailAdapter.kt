package com.example.restaurantapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.databinding.ItemOrderBinding
import com.example.restaurantapp2.databinding.ItemOrderDetailBinding
import com.example.restaurantapp2.models.OrderDetailResponse
import com.example.restaurantapp2.models.OrderResponse

class OrderDetailAdapter() : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    private val orderDetailList = mutableListOf<OrderDetailResponse>()


    inner class OrderDetailViewHolder(private val binding : ItemOrderDetailBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : OrderDetailResponse){
            binding.tvQuantity.text = "${item.quantity}x"
            binding.tvProductName.text = item.productName
            binding.tvUnitPrice.text = convertedPrice((item.unitPrice * item.quantity))
            binding.tvFinalPrice.visibility = View.GONE
            if(item.discountPercent >0){
                binding.tvDiscountPercent.text = "-${item.discountPercent}%"
                binding.tvDiscountPercent.visibility = View.VISIBLE
                binding.tvFinalPrice.visibility =View.VISIBLE
                val discountedPrice = item.unitPrice * (1 - item.discountPercent / 100.0)
                binding.tvFinalPrice.text = convertedPrice(discountedPrice * item.quantity)
                binding.tvUnitPrice.paintFlags =
                    binding.tvUnitPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            }
            else{
                binding.tvDiscountPercent.visibility = View.GONE
                binding.tvFinalPrice.visibility = View.GONE


                binding.tvUnitPrice.paintFlags =
                    binding.tvUnitPrice.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            if(item.note.isNullOrBlank()){
                binding.tvNote.visibility = View.GONE
            }
            else{
                binding.tvNote.visibility =View.VISIBLE
                binding.tvNote.text = item.note
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val binding = ItemOrderDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderDetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderDetailList.size
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        Log.d("DEBUG", "Binding position = $position")
        val item = orderDetailList[position]
        holder.bind(item)
    }

    fun updateData(newList: List<OrderDetailResponse>) {
        orderDetailList.clear()
        orderDetailList.addAll(newList)
        notifyDataSetChanged()
    }
}