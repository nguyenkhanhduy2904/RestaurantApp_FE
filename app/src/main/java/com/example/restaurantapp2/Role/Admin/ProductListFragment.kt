package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment: Fragment(R.layout.fragment_product_list) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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