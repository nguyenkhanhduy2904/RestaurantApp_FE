package com.example.restaurantapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.databinding.ItemHeaderBinding
import com.example.restaurantapp2.databinding.ItemProductBinding
import com.example.restaurantapp2.models.Category

class HeaderAdapterAdmin(
    private val categories: MutableList<Category> = mutableListOf<Category>(),
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<HeaderAdapterAdmin.HeaderViewHolder>() {

    private val categoryAdapter = CategoryAdapterAdmin(mutableListOf(), onCategoryClick)
    inner class HeaderViewHolder(private val binding: ItemHeaderBinding)
        : RecyclerView.ViewHolder(binding.root) {



        init {
            binding.rvCategoryList.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = categoryAdapter
            }
        }

        fun bind(categories: List<Category>) {
            categoryAdapter.updateList(categories)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HeaderViewHolder(
            ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount() = 1 // always just 1 header

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(categories)
    }

    fun updateList(newList: List<Category>) {
        Log.d("HeaderAdapter", "updating with ${newList.size} categories")
        categories.clear()
        categories.addAll(newList)
        notifyItemChanged(0) // better than notifyDataSetChanged()
    }


    fun updateSelectedCategory(categoryId: Int?) {
        categoryAdapter.updateSelectedCategory(categoryId)
    }
}