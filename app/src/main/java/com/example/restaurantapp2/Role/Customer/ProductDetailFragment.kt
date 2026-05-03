package com.example.restaurantapp2.Role.Customer

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.restaurantapp2.R
import com.example.restaurantapp2.Utils.convertedPrice
import com.example.restaurantapp2.databinding.FragmentProductDetailBinding
import com.example.restaurantapp2.models.CartItem
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.viewmodels.CartVM
import com.example.restaurantapp2.viewmodels.ProductVM
import com.example.restaurantapp2.viewmodels.UserVM

class ProductDetailFragment: Fragment(R.layout.fragment_product_detail) {


    private val productVM : ProductVM by activityViewModels()
    private val cartVM: CartVM by lazy {
        (requireActivity() as CustomerActivity).cartVM
    }
    private val userVM : UserVM by activityViewModels()
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        _binding = FragmentProductDetailBinding.bind(view)
        binding.btnBack.setOnClickListener{
            parentFragmentManager.popBackStack()
        }


        val productId = arguments?.getInt("productId") ?: return
        val cachedProduct = productVM.allProducts.find { it.productId == productId }

        if(cachedProduct !=null){
             bindProduct(cachedProduct)
        }
        else{
            productVM.selectedProduct.observe(viewLifecycleOwner) { product -> bindProduct(product) }// this line will wait for data arrive?
            productVM.getProductById(productId)
        }
        binding.btnAddToCart.setOnClickListener {
            Log.d("TEST", "clicked")

            cartVM.insert(CartItem(
                productId = productId,
                quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 1,
                note = binding.etNote.text.toString(),
                userId = userVM.user.value?.userId ?: 0
            ))
            parentFragmentManager.popBackStack()
        }
    }

    private fun bindProduct(product: Product){
        binding.productNameTextView.text = product.productName ?: "Unknown Product"
        binding.productDescTextView.text = product.productDescription ?: "No description available"
        if (binding.etQuantity.text.isNullOrEmpty()) {
            binding.etQuantity.setText("1")
        }
        Glide.with(binding.root)
            .load(product.productThumbnailUrl)
            .placeholder(R.drawable.default_food_img)
            .into(binding.productImageView)

        if(product.isDiscounted) {
            binding.productFinalPriceTextView.text = "Price: ${convertedPrice(product.finalPrice) ?: "N/A"}"
            binding.productDiscountTextView.text = "Discount: ${product.priceReduction ?: "N/A"}%"
            binding.productDiscountTextView.visibility = View.VISIBLE
        } else {
            binding.productFinalPriceTextView.text = "Price: ${convertedPrice(product.finalPrice)}"
            binding.productDiscountTextView.visibility = View.GONE

        }
        binding.btnPlus.setOnClickListener {
            val current = binding.etQuantity.text.toString().toIntOrNull() ?: 1
            binding.etQuantity.setText((current + 1).toString())
            binding.btnMinus.isEnabled = true
        }
        binding.btnMinus.setOnClickListener {
            val current = binding.etQuantity.text.toString().toIntOrNull() ?: 1
            if (current > 1) {
                binding.etQuantity.setText((current - 1).toString())
            }else{
                binding.btnMinus.isEnabled = false
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        (activity as CustomerActivity).hideBottomNavBar()
    }
    override fun onPause() {
        super.onPause()
        (activity as CustomerActivity).showBottomNavBar()
    }

}