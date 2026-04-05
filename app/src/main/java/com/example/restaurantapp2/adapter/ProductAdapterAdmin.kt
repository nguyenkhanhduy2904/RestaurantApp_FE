package com.example.restaurantapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantapp2.R
import com.example.restaurantapp2.databinding.ItemAdminProductBinding
import com.example.restaurantapp2.models.Product

class ProductAdapterAdmin (
    private val products : List<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
): RecyclerView.Adapter<ProductAdapterAdmin.ProductViewHolder>() {


    inner class ProductViewHolder(private val binding : ItemAdminProductBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product){
            binding.txtFoodName.text = product.productName
            binding.txtFoodCategory.text = product.categoryId.toString()
            binding.txtFoodPrice.text = product.finalPrice.toString()
            binding.btnEdit.setOnClickListener {
                onEditClick(product)
            }
            if(product.isDiscounted){
                binding.txtPriceReduction.visibility = View.VISIBLE
                binding.txtPriceReduction.text = product.productPriceReduction.toString()
            }
            else{
                binding.txtPriceReduction.visibility = View.GONE
                binding.txtPriceReduction.text = ""
            }
            Glide.with(binding.root).load(product.productThumbnailUrl).placeholder(R.drawable.default_food_img).into(binding.ivFoodImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemAdminProductBinding.inflate(
            LayoutInflater.from(parent.context),parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])


    }

    fun updateList(newProducts: List<Product>) {
        Log.d("ProductAdapter", "updating with ${newProducts.size} products")
        newProducts.forEach { Log.d("ProductAdapter", "Product: ${it.categoryId}") }
        (products as MutableList).clear()
        (products as MutableList).addAll(newProducts)
        notifyDataSetChanged()
    }
}