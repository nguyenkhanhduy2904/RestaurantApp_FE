package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.R
import com.example.restaurantapp2.adapter.HeaderAdapterAdmin
import com.example.restaurantapp2.adapter.ProductAdapterAdmin
import com.example.restaurantapp2.models.Category
import com.example.restaurantapp2.models.CategoryRequest
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
//                Toast.makeText(requireContext(), "Edit clicked for ${product.productName}", Toast.LENGTH_SHORT).show()
                Log.d("Product Id:", "${product.productId}")
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, CreateProductFragment::class.java, bundle)
                    addToBackStack(null)
                    commit()
                }
//
            },

            categoryMap = emptyMap(),  // will be updated later when categories are loaded


            onDeleteClick = { product ->
                // handle delete click, e.g. show confirmation dialog and delete product
                Log.d("ProductListFragment", "Delete clicked for ${product.productName}")
            }
        )
        val header = HeaderAdapterAdmin(mutableListOf(),
            onCategoryClick = { category ->
                // handle category click, e.g. filter products by category
                if(category.categoryId==-1){
                    CreateCategoryDialogFragment{
                        name ->
//                        Toast.makeText(requireContext(), "New category name: $name", Toast.LENGTH_SHORT).show()

                        val cate = CategoryRequest(categoryId = 0, categoryName = name, status = "ACTIVE")   // id will be assigned by backend

                        categoryVM.createCategory(cate)
                    }.show(parentFragmentManager, "CreateCategoryDialog")

                }
                else {
                    viewModel.filterProductsByCategory(category.categoryId)
//                    Toast.makeText(
//                        requireContext(),
//                        "Category clicked: ${category.categoryName}",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    Log.d(
                        "ProductListFragment",
                        "Category clicked: ${category.categoryName}" + "All product: " + viewModel.allProducts.size + "Filtered product: " + viewModel.products.value?.size
                    )
                }


            },

        )

        view.findViewById<RecyclerView>(R.id.rvCartList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = ConcatAdapter(header,adapter)
        }
        val etSearch = view.findViewById<EditText>(R.id.edtSearch)

        etSearch.addTextChangedListener {
            val query = it.toString()
            viewModel.searchProducts(query)
        }

        viewModel.loadProducts()

        viewModel.selectedCategoryId.observe(viewLifecycleOwner){
            header.updateSelectedCategory(it)
        }


        categoryVM.categories.observe(viewLifecycleOwner) {

            Log.d("CATEGORY_DEBUG", "Observed size: ${it.size}")

            val displayList = it.toMutableList()

            displayList.add(Category(categoryId = -1, categoryName = "Add", status = "ACTIVE"))  // Add a special category for "Add"

            header.updateList(displayList)

            val map = it.associate {category -> category.categoryId to category.categoryName }
            adapter.updateCategoryMap(map)
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