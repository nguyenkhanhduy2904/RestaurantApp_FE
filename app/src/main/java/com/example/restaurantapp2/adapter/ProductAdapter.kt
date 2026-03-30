package com.example.restaurantapp2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantapp2.R
import com.example.restaurantapp2.databinding.ItemProductBinding
import com.example.restaurantapp2.models.Product

class ProductAdapter(
    private val products : List<Product>
): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    inner class ProductViewHolder(private val binding : ItemProductBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(product: Product){
            binding.txtFoodName.text = product.productName
            binding.txtFoodCategory.text = product.productCategory.toString()
            binding.txtFoodPrice.text = product.finalPrice.toString()

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
       val binding = ItemProductBinding.inflate(
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
        (products as MutableList).clear()
        (products as MutableList).addAll(newProducts)
        notifyDataSetChanged()
    }


}