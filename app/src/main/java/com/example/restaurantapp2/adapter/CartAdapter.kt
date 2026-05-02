package com.example.restaurantapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.databinding.ItemCartBinding
import com.example.restaurantapp2.models.CartItem
import com.example.restaurantapp2.models.CartUI

class CartAdapter(
    val onIncreaseClick: (CartItem) -> Unit = {},
    val onDecreaseClick: (CartItem) -> Unit = {},
    val onDeleteClick : (CartItem) -> Unit = {},
    val onNoteChange: (CartItem, String) -> Unit = { _, _ -> }
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var items: List<CartUI> = emptyList()

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartUI) {

            val product = item.product

            val cart = item.cart

            binding.txtFoodName.text =
                product?.productName ?: "Unknown"

            binding.txtFoodPrice.text =
                product?.productPrice?.let { convertedPrice(it) } ?: "0đ"


            binding.etQuantity.setText(cart.quantity.toString())


            Glide.with(binding.root)
                .load(product?.productThumbnailUrl)
                .placeholder(R.drawable.default_food_img)
                .into(binding.ivFoodImage)

            binding.btnDelete.setOnClickListener {
                val newNote = binding.etNote.text.toString()
                if (newNote != cart.note) {
                    onNoteChange(cart, newNote)
                }
                onDeleteClick(cart)
            }
            binding.btnIncrease.setOnClickListener {
                val newNote = binding.etNote.text.toString()
                if (newNote != cart.note) {
                    onNoteChange(cart, newNote)
                }
                onIncreaseClick(cart)
            }
            binding.btnDecrease.setOnClickListener {
                val newNote = binding.etNote.text.toString()
                if (newNote != cart.note) {
                    onNoteChange(cart, newNote)
                }
                onDecreaseClick(cart)
            }


            binding.etNote.setOnFocusChangeListener(null)
            binding.etNote.setOnEditorActionListener(null)

            if (binding.etNote.text.toString() != cart.note) {
                binding.etNote.setText(cart.note)
            }

            binding.etNote.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newNote = binding.etNote.text.toString()
                    if (newNote != cart.note) {
                        onNoteChange(cart, newNote)
                    }
                }
            }

            binding.etNote.setOnEditorActionListener { _, _, _ ->
                val newNote = binding.etNote.text.toString()
                if (newNote != cart.note) {
                    onNoteChange(cart, newNote)
                }
                false
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun submitList(newList: List<CartUI>) {
        items = newList
        notifyDataSetChanged()
    }
}