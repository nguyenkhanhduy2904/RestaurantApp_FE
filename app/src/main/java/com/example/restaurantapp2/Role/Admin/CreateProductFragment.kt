package com.example.restaurantapp2.Role.Admin

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.restaurantapp2.models.Product
import com.example.restaurantapp2.models.ProductRequest
import com.example.restaurantapp2.network.CloudinaryService
import com.example.restaurantapp2.viewmodels.ProductVM
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateProductFragment : Fragment(R.layout.add_product_layout) {

    private lateinit var btnCancel : Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnPickImage: ImageButton

    private var imageUri : Uri? = null
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it

                val btnPickImage = requireView().findViewById<ImageButton>(R.id.btnPickImage)

                // show selected image
                btnPickImage.setImageURI(it)

                //remove padding/icon effect
                btnPickImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

    private val vm: ProductVM by viewModels()

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

        btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnBack = view.findViewById<ImageButton>(R.id.ibtnBack)
        btnSave = view.findViewById<Button>(R.id.btnSave)

        btnPickImage = view.findViewById<ImageButton>(R.id.btnPickImage)

        btnCancel.setOnClickListener {
            handleBackFunction()
        }
        btnBack.setOnClickListener{
            handleBackFunction()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackFunction()
        }
        btnPickImage.setOnClickListener{
            pickImage.launch("image/*")
        }

        btnSave.setOnClickListener{
            handleCreateProduct()
        }

        vm.createStatus.observe(viewLifecycleOwner) { success ->
            btnSave.isEnabled = true

            if (success) {
                Toast.makeText(requireContext(), "Product created", Toast.LENGTH_SHORT).show()
                handleBackFunction()
            } else {
                Toast.makeText(requireContext(), "Failed to create product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleBackFunction() {
        parentFragmentManager.popBackStack()
    }

    fun handleCreateProduct() {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please choose an image", Toast.LENGTH_SHORT).show()
            return
        }
        btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val imgUrl = CloudinaryService.uploadImage(imageUri!!, requireContext())

                val productName = requireView()
                    .findViewById<EditText>(R.id.txtProductName).text.toString()

                val productPrice = requireView()
                    .findViewById<EditText>(R.id.txtProductPrice)
                    .text.toString().toDoubleOrNull()

                val productDescription = requireView()
                    .findViewById<EditText>(R.id.txtProductDescription).text.toString()

                val productCategory = 1

                val request = ProductRequest(
                    productName = productName,
                    productPrice = productPrice ?: 0.0,
                    productDescription = productDescription,
                    productThumbnailUrl = imgUrl,
                    productCategory = productCategory
                )

                //Debug JSON
                Log.d("JSON", Gson().toJson(request))

                //Call ViewModel on MAIN thread
                vm.createProduct(request)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnSave.isEnabled = true
            }
        }
    }





}