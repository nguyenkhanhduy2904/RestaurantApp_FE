package com.example.restaurantapp2.Role.Admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.addCallback

class CreateProductFragment : Fragment(R.layout.add_product_layout) {

    override fun onResume() {
        super.onResume()

        (activity as AdminActivity).hideBottomNavBar()
    }

    override fun onPause() {
        super.onPause()

        (activity as AdminActivity).showBottomNavBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCancel = view.findViewById<View>(R.id.btnCancel)
        val btnBack = view.findViewById<View>(R.id.ibtnBack)

        btnCancel.setOnClickListener {
            handleBackFunction()
        }
        btnBack.setOnClickListener{
            handleBackFunction()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackFunction()
        }

    }

    fun handleBackFunction() {
        Toast.makeText(requireContext(), "Product creation cancelled", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }


}