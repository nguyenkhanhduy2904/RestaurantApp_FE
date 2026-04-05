package com.example.restaurantapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.databinding.ItemCategoryBinding
import com.example.restaurantapp2.models.Category

class CategoryAdapterAdmin(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapterAdmin.CategoryViewHolder>() {



    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.txtCategoryName.text = category.categoryName
            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }
    fun updateList(newList: List<Category>) {
        Log.d("CategoryAdapter", "updating with ${newList.size} categories")
        (categories as MutableList).clear()
        (categories as MutableList).addAll(newList)
        notifyDataSetChanged()

    }




}