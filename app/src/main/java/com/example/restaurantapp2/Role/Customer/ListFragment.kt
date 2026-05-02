package com.example.restaurantapp2.Role.Customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.adapter.ProductAdapter

import com.example.restaurantapp2.viewmodels.ProductVM
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.example.restaurantapp2.R
import com.example.restaurantapp2.adapter.HeaderAdapterAdmin
import com.example.restaurantapp2.models.CartItem
import com.example.restaurantapp2.viewmodels.CartVM
import com.example.restaurantapp2.viewmodels.CategoryVM
import com.example.restaurantapp2.viewmodels.UserVM


class ListFragment: Fragment(R.layout.fragment_list){

    private val productVM : ProductVM by viewModels()
    private val cartVM: CartVM by lazy {
        (requireActivity() as CustomerActivity).cartVM
    }
    private val categoryVM : CategoryVM by activityViewModels()
    private val userVM: UserVM by activityViewModels()

    private var productsReady = false
    private var categoriesReady = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearch = view.findViewById<EditText>(R.id.edtSearch)

        etSearch.addTextChangedListener {
            val query = it.toString()
            productVM.searchProducts(query)
        }







        val adapter = ProductAdapter(
            products = mutableListOf(),

        onAddClick = { product ->
            val cartItem = CartItem(
                productId = product.productId,
                quantity = 1,
                note = "",
                userId = userVM.user.value?.userId ?: 0
            )
            cartVM.insert(cartItem)
        },

            categoryMap = emptyMap(),

        onViewDetailClick = { product ->
           val bundle = Bundle().apply {
               putInt("productId", product.productId)
           }
            val detailFragment = ProductDetailFragment()
            detailFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, detailFragment)
                .addToBackStack(null)
                .commit()

        }
        )

        val header = HeaderAdapterAdmin(mutableListOf(),
            onCategoryClick = { category ->

                if(category.categoryId==-1){

                    //hide this cate

                }
                else {
                    productVM.filterProductsByCategory(category.categoryId)
                    Toast.makeText(
                        requireContext(),
                        "Category clicked: ${category.categoryName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "ProductListFragment",
                        "Category clicked: ${category.categoryName}" + "All product: " + productVM.allProducts.size + "Filtered product: " + productVM.products.value?.size
                    )
                }


            },

        )

        view.findViewById<RecyclerView>(R.id.rvCartList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = ConcatAdapter(header,adapter)
        }

        productVM.products.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                productsReady = true
                adapter.updateList(list)
                tryRender()
            } else {
                Log.d("ListFragment", "Products empty or not ready yet")
            }
        }
        categoryVM.categories.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                categoriesReady = true
                header.updateList(list.toMutableList())
                val map = list.associate { it.categoryId to it.categoryName }
                adapter.updateCategoryMap(map)
                tryRender()
            } else {
                Log.d("ListFragment", "Categories empty or not ready yet")
            }
        }

        productVM.selectedCategoryId.observe(viewLifecycleOwner) {
            header.updateSelectedCategory(it)
        }
    }
    private fun tryRender() {
        if (productsReady && categoriesReady) {
            Log.d("ListFragment", "Both ready — UI fully loaded")
        }
    }



}