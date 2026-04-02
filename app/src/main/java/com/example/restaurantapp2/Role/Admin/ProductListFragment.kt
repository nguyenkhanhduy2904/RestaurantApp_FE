package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.adapter.HeaderAdapter
import com.example.restaurantapp2.adapter.ProductAdapterAdmin
import com.example.restaurantapp2.viewmodels.ProductVM
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment: Fragment(R.layout.fragment_product_list) {

    private val viewModel : ProductVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductAdapterAdmin(mutableListOf(),
            onEditClick = { product ->
                val bundle = Bundle().apply {
                    putInt("productId", product.productId)
                }
                // handle edit click, e.g. navigate to edit screen with product details
                Toast.makeText(requireContext(), "Edit clicked for ${product.productName}", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, CreateProductFragment::class.java, bundle)
                    addToBackStack(null)
                    commit()

                }
//
            },
            onDeleteClick = { product ->
                // handle delete click, e.g. show confirmation dialog and delete product
                Log.d("ProductListFragment", "Delete clicked for ${product.productName}")
            }
        )
        val header = HeaderAdapter()
        view.findViewById<RecyclerView>(R.id.rvProductList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = ConcatAdapter(header,adapter)
        }

        viewModel.products.observe(viewLifecycleOwner){
            Log.d("ListFragment", "received ${it.size} products")
            adapter.updateList(it)
        }


        val fabAddProd = view.findViewById<FloatingActionButton>(R.id.fab_add_product)


        fabAddProd.setOnClickListener() {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, CreateProductFragment())
                addToBackStack(null)
                commit()

            }
        }


    }
}