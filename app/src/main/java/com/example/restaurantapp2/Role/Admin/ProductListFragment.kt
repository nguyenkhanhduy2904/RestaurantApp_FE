package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.adapter.HeaderAdapter
import com.example.restaurantapp2.adapter.HeaderAdapterAdmin
import com.example.restaurantapp2.adapter.ProductAdapterAdmin
import com.example.restaurantapp2.viewmodels.CategoryVM
import com.example.restaurantapp2.viewmodels.ProductVM
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment: Fragment(R.layout.fragment_product_list) {

    private val viewModel: ProductVM by activityViewModels()
    private val categoryVM: CategoryVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ProductAdapterAdmin(mutableListOf(),
            onEditClick = { product ->
                val bundle = Bundle().apply {
                    putInt("productId", product.productId)
                }
                // handle edit click, e.g. navigate to edit screen with product details
                Toast.makeText(requireContext(), "Edit clicked for ${product.productName}", Toast.LENGTH_SHORT).show()
                Log.d("Product Id:", "${product.productId}")
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
        val header = HeaderAdapterAdmin(mutableListOf(),
            onCategoryClick = { category ->
                // handle category click, e.g. filter products by category
                viewModel.filterProductsByCategory(category.categoryId)
                Toast.makeText(requireContext(), "Category clicked: ${category.categoryName}", Toast.LENGTH_SHORT).show()
                Log.d("ProductListFragment", "Category clicked: ${category.categoryName}" + "All product: "+  viewModel.allProducts.size + "Filtered product: " + viewModel.products.value?.size)

            }
        )





        view.findViewById<RecyclerView>(R.id.rvProductList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = ConcatAdapter(header,adapter)
        }

        viewModel.loadProducts()

        categoryVM.categories.observe(viewLifecycleOwner) {
            Log.d("CATEGORY_DEBUG", "Observed size: ${it.size}")
            header.updateList(it)
        }

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }


        viewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                viewModel.loadProducts()
                viewModel.resetUpdateStatus()
            }
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