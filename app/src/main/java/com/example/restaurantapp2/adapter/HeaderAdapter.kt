package com.example.restaurantapp2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.databinding.ItemHeaderBinding
import com.example.restaurantapp2.databinding.ItemProductBinding
import com.example.restaurantapp2.models.Category

class HeaderAdapter (
    private val categories: MutableList<Category> = mutableListOf<Category>(),
    private val onCategoryClick: (Category) -> Unit
): RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            // setup your search bar, menu, etc.

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HeaderViewHolder(
            ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount() = 1 // always just 1 header

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) = holder.bind()
}