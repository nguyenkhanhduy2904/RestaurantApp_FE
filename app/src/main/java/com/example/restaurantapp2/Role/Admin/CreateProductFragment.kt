package com.example.restaurantapp2.Role.Admin

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.restaurantapp2.R
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.restaurantapp2.models.Category
import com.example.restaurantapp2.models.ProductRequest
import com.example.restaurantapp2.network.CloudinaryService
import com.example.restaurantapp2.viewmodels.CategoryVM
import com.example.restaurantapp2.viewmodels.ProductVM
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CreateProductFragment : Fragment(R.layout.add_product_layout2) {

//    val productId = arguments?.getInt("productId") ?: -1

    var productId : Int = -1
    var isEditMode : Boolean = false

    var selectedCategoryId: Int? = null
    var cateIndex : Int =-1

    private lateinit var btnCancel : Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnPickImage: ImageButton
    private lateinit var spnCategoryId: Spinner

    private lateinit var slider : Slider
    private lateinit var switch : SwitchMaterial
    private lateinit var txtReduction : TextView

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
    private val categoryVM: CategoryVM by viewModels()

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

        productId = arguments?.getInt("productId") ?: -1





        btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnBack = view.findViewById<ImageButton>(R.id.ibtnBack)
        btnSave = view.findViewById<Button>(R.id.btnNextAction)
        spnCategoryId = view.findViewById<Spinner>(R.id.spnCategory)

        slider = view.findViewById<Slider>(R.id.slPriceReduce)
        switch = view.findViewById<SwitchMaterial>(R.id.switchOption)

        val spinnerAdapter = ArrayAdapter<Category>( this.requireContext(), android.R.layout.simple_spinner_item, mutableListOf())

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCategoryId.adapter = spinnerAdapter

        categoryVM.categories.observe(viewLifecycleOwner){ categories ->
            spinnerAdapter.clear()
            spinnerAdapter.addAll(categories)
            spinnerAdapter.notifyDataSetChanged()

            trySetSpinnerSelection()
        }

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
           handleSaveButton()
        }
        txtReduction = view.findViewById<TextView>(R.id.txtProductPriceReduction)
        slider.addOnChangeListener { _, value, _ ->
            txtReduction.text = "${value.toFloat()}%"
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
        vm.updateStatus.observe(viewLifecycleOwner) { success ->
            btnSave.isEnabled = true
            if (success) {
                Toast.makeText(requireContext(), "Product updated", Toast.LENGTH_SHORT).show()
                handleBackFunction()
            } else {
                Toast.makeText(requireContext(), "Failed to update product", Toast.LENGTH_SHORT).show()
            }
        }
        vm.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                Log.d("error message",message);
            }
        }

        if(productId ==-1){
            //create mode, do nothing
            isEditMode = false
        }
        else{
            isEditMode = true
            //edit mode, load product details and populate fields
            loadProductDetails(productId)


        }



    }

    fun loadProductDetails(productId: Int) {

        vm.getProductById(productId)

        Log.d("CreateProductFragment", "Loading product details for ID: $productId")
        vm.selectedProduct.observe(viewLifecycleOwner){ product ->
            requireView().findViewById<EditText>(R.id.txtProductName).setText(product.productName)
            requireView().findViewById<EditText>(R.id.txtProductPrice).setText(product.productPrice.toString())
            requireView().findViewById<EditText>(R.id.txtProductDescription).setText(product.productDescription)

            selectedCategoryId = product.categoryId

            slider.value = product.priceReduction.toFloat()
            switch.isChecked = product.status == "ACTIVE"


            Glide.with(requireContext()).load(product.productThumbnailUrl).placeholder(R.drawable.default_food_img).into(requireView().findViewById<ImageButton>(R.id.btnPickImage))

            trySetSpinnerSelection()

        }
        Log.d("check product id", productId.toString())




    }

    fun trySetSpinnerSelection() {
        val categories = categoryVM.categories.value
        val categoryId = selectedCategoryId

        if (categories != null && categoryId != null) {
            val index = categories.indexOfFirst { it.categoryId == categoryId }

            if (index != -1) {
                spnCategoryId.setSelection(index)
            }
        }
    }


    fun handleBackFunction() {
        parentFragmentManager.popBackStack()
    }
    fun handleSaveButton() {
        if (imageUri == null && !isEditMode) {
            Toast.makeText(requireContext(), "Please choose an image", Toast.LENGTH_SHORT).show()
            return
        }
        btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val imgUrl = if (imageUri != null) {
                    val uploadedUrl = CloudinaryService.uploadImage(imageUri!!, requireContext())

                    if (uploadedUrl.isNullOrEmpty()) {
                        throw Exception("Image upload failed")
                    }

                    uploadedUrl
                } else {
                    vm.selectedProduct.value?.productThumbnailUrl
                        ?: throw IllegalStateException("Missing existing image")
                }

                val productName = requireView()
                    .findViewById<EditText>(R.id.txtProductName).text.toString()

                val productPrice = requireView()
                    .findViewById<EditText>(R.id.txtProductPrice)
                    .text.toString().toDoubleOrNull()

                val productDescription = requireView()
                    .findViewById<EditText>(R.id.txtProductDescription).text.toString()

//                val productCategory = 1// find the spinner and get selected category id
                val productCategory = (spnCategoryId.selectedItem as? Category)?.categoryId
                    ?: throw IllegalStateException("No category selected")

                val status = if (switch.isChecked) "ACTIVE" else "NOT ACTIVE"
                val reduction = slider.value

                val request = ProductRequest(
                    productId = if(isEditMode) productId else null,
                    productName = productName,
                    productPrice = productPrice ?: 0.0,
                    productDescription = productDescription,
                    productThumbnailUrl = imgUrl,
                    categoryId = productCategory,
                    status = status,
                    priceReduction = reduction
                )
                Log.d("Prod request", request.toString())
;


                //Debug JSON
                Log.d("JSON", Gson().toJson(request))
                if(isEditMode){
                    vm.updateProduct(productId, request)
                }
                else{
                    vm.createProduct(request)
                }

                //Call ViewModel on MAIN thread


            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                btnSave.isEnabled = true
            }
        }

    }









}