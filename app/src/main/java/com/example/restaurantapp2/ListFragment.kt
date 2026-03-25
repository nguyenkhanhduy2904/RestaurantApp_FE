package com.example.restaurantapp2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapp2.adapter.ProductAdapter

import com.example.restaurantapp2.viewmodels.ProductVM
import androidx.fragment.app.viewModels


class ListFragment: Fragment(R.layout.fragment_list){

    private val viewModel : ProductVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ProductAdapter(mutableListOf())
        view.findViewById<RecyclerView>(R.id.rvProductList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        viewModel.products.observe(viewLifecycleOwner){
            Log.d("ListFragment", "received ${it.size} products")
            adapter.updateList(it)
        }
    }


}